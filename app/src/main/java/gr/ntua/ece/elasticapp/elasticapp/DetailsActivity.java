package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
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
                            ((TextView) findViewById(R.id.DetailsType)).setText(jsonResults.getString("types"));
                            ((TextView) findViewById(R.id.DetailsPhone)).setText(jsonResults.getString("formatted_phone_number"));
                            ((RatingBar) findViewById(R.id.DetailsRatingBar)).setRating(Float.parseFloat(jsonResults.getString("rating")));
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
