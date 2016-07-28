package gr.ntua.ece.elasticapp.elasticapp;

import android.content.pm.PackageManager;
import android.location.LocationManager;
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
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    MyCustomAdapter dataAdapter;
    LocListener locListener;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locListener = new LocListener();
        locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5000, 10, locListener);
        } catch (SecurityException e){
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.listView);
        displayListView();
    }

    private void displayListView() {

        dataAdapter = new MyCustomAdapter(this, R.layout.live_result_item);

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
                    search(s.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearchByName(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
    }

    private void performSearchByName(String text) {
        Intent intent = new Intent(this, ResultsActivity.class);
        intent.putExtra("text", text);

        if (((Switch) findViewById(R.id.NearMe)).isChecked()) {
            intent.putExtra("lat", locListener.getLat());
            intent.putExtra("lon", locListener.getLon());
        }
        startActivity(intent);
    }

    //TODO na ftiaksoume alli search me near me
    private void search(String searchText) throws JSONException {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, "http://83.212.96.164/searchapp/rest/name/?search=" + searchText, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
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
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                    }
                }
        ) {
        };
        int socketTimeout = 30000; //milliseconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        postRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(postRequest, "json_obj_req");
    }

    private class MyCustomAdapter extends ArrayAdapter<LiveResult> {

        public MyCustomAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        private class ViewHolder {
            TextView code;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.live_result_item, parent, false);
                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            LiveResult result = this.getItem(position);
            holder.code.setText(result.getName());

            return convertView;
        }
    }
}
