package gr.ntua.ece.elasticapp.elasticapp;

import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationServices;

import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener{

    private ListView listView;
    private MainCustomAdapter dataAdapter;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private final static int MY_PERMISSION_ACCESS_FINE_LOCATION = 106;
    private Location mLastLocation;
    private MainCommunicator mainCommunicator = new MainCommunicator();

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // First we need to check availability of play services
        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        findViewById(R.id.NearMe).setEnabled(false);

        listView = (ListView) findViewById(R.id.listView);
        displayListView();
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        findViewById(R.id.NearMe).setEnabled(true);
        Log.d("connected", "done");
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    private void displayListView() {

        dataAdapter = new MainCustomAdapter(this, R.layout.live_result_item, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));

        listView.setAdapter(dataAdapter);
        listView.setTextFilterEnabled(true);
        listView.setEmptyView(findViewById(R.id.emptyElement));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                LiveResult selectedItem = (LiveResult) parent.getItemAtPosition(position);


                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("id", selectedItem.getId());
                startActivity(intent);
            }
        });
        EditText editText = (EditText) findViewById(R.id.myFilter);
        editText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                AppController.getInstance().cancelPendingRequests();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                dataAdapter.clear();
                try {
                    String url = "http://83.212.96.164/searchapp/rest/name/?search=" + s.toString();
                    mainCommunicator.search(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    goToResultsActivity(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void goToResultsActivity(String text) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("text", text);

        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  }, MY_PERMISSION_ACCESS_FINE_LOCATION );
        }

        if (((Switch) findViewById(R.id.NearMe)).isChecked()) {
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                double latitude = mLastLocation.getLatitude();
                double longitude = mLastLocation.getLongitude();
                intent.putExtra("lat", latitude);
                intent.putExtra("lon", longitude);

            }
        }
        startActivity(intent);
    }

    private class MainCommunicator extends HttpCommunicator {

        public void processResults(JSONObject jsonResponse){
            try {
                JSONArray jsonResults = jsonResponse.getJSONArray("res");
                dataAdapter.clear();
                for (int i = 0; i < jsonResults.length(); i++) {
                    LiveResult res = new LiveResult();
                    res.setName(jsonResults.getJSONObject(i).getString("name"));
                    res.setId(jsonResults.getJSONObject(i).getString("id"));
                    dataAdapter.add(res);
                }
                dataAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
