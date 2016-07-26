package gr.ntua.ece.elasticapp.elasticapp;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;


public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap googleMap;




    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        setUpMap();

    }
    public void setUpMap(){

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.setTrafficEnabled(true);
        googleMap.setIndoorEnabled(true);
        googleMap.setBuildingsEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.DetailsMap);
        mapFragment.getMapAsync(this);
        String id = (String)getIntent().getSerializableExtra("id");

        try {
            search(id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO 23/7 : add map
    }



    private void search(String searchText) throws JSONException {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, "http://83.212.96.164/searchapp/rest/id/?id=" + searchText, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            JSONObject jsonResults = jsonResponse.getJSONObject("res");

                            ((TextView) findViewById(R.id.DetailsName)).setText(jsonResults.getString("name"));
                            ((TextView) findViewById(R.id.DetailsAddress)).setText(jsonResults.getString("formatted_address"));
                            ((TextView) findViewById(R.id.DetailsWebsite)).setText(jsonResults.getString("website"));
                            ((TextView) findViewById(R.id.DetailsPhone)).setText(jsonResults.getString("formatted_phone_number"));
                            ((RatingBar) findViewById(R.id.DetailsRatingBar)).setRating(Float.parseFloat(jsonResults.getString("rating")));
                            String lat = jsonResults.getString("lat");
                            String lon = jsonResults.getString("lon");
                            Log.d("coords:","("+lat+", "+lon+")");
                            LatLng coords = new LatLng( Double.parseDouble(lat), Double.parseDouble(lon));
                            googleMap.addMarker(new MarkerOptions().position(coords).title(jsonResults.getString("name")));
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(coords) // Center Set
                                    .zoom(16.0f)                // Zoom
                                    .bearing(90)                // Orientation of the camera to east
                                    .build();                   // Creates a CameraPosition from the builder
                            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
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
}
