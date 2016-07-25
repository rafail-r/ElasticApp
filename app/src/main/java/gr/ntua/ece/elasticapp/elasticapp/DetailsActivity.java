package gr.ntua.ece.elasticapp.elasticapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        String id = (String)getIntent().getSerializableExtra("id");
        Log.d("placeid", id);
        Place toShow = new Place();
//        try {
//            search(id);
//            Log.d("placeid", toShow.getId());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        // TODO 23/7 : add map
    }

    // TODO 25/7 : add adapters etc
    private void search(String searchText) throws JSONException {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, "http://83.212.96.164/searchapp/rest/id/?id=" + searchText, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        Log.d("before try", jsonResponse.toString());
//                        try {
//                            JSONObject jsonResults = jsonResponse.getJSONObject("res");
//                            Log.d("after res", "ssss");
//                            toShow.setName(jsonResults.getString("name"));
//                            Log.d("after name", "ssss");
//                            res.setId(jsonResults.getString("id"));
//                            Log.d("after id", "ssss");
//                            res.setAddress(jsonResults.getString("formatted_address"));
//                            res.setType(jsonResults.getString("types"));
//                            res.setRating(jsonResults.getString("rating"));
//                            res.setPhoneNumber(jsonResults.getString("formatted_phone_number"));
//                            Log.d("end of try", "ssss");
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
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
