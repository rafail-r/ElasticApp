package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class ResultsActivity extends AppCompatActivity {

    ListView listView;
    MyCustomAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Log.d("searchText",(String)getIntent().getSerializableExtra("text"));
        listView = (ListView) findViewById(R.id.listView);
        displayListView((String)getIntent().getSerializableExtra("text"));
    }

    private void displayListView(String searchText) {
        ArrayList<Place> results_List = new ArrayList<>();

        dataAdapter = new MyCustomAdapter(this, R.layout.result_item, results_List);

        listView.setAdapter(dataAdapter);
        listView.setTextFilterEnabled(true);
        listView.setEmptyView(findViewById(R.id.emptyElement));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Place selectedItem = (Place) parent.getItemAtPosition(position);

                Intent intent = new Intent(ResultsActivity.this, DetailsActivity.class);
                intent.putExtra("id", selectedItem.getId());
                startActivity(intent);
            }
        });
        dataAdapter.clear();
        try {
            search(searchText);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class MyCustomAdapter extends ArrayAdapter<Place> {

        private ArrayList<Place> results;


        public MyCustomAdapter(Context context, int textViewResourceId, ArrayList<Place> results_List) {
            super(context, textViewResourceId, results_List);
            this.results = new ArrayList<>();
            this.results.addAll(results_List);
        }


        private class ViewHolder {
            TextView name;
            TextView address;
            TextView type;
            RatingBar ratingBar;
        }

        @Override
        public int getCount() {

            return super.getCount();


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {

                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                convertView = vi.inflate(R.layout.result_item, parent, false);
                holder = new ViewHolder();

                holder.name = (TextView) convertView.findViewById(R.id.TextName);
                holder.address = (TextView) convertView.findViewById(R.id.TextAddress);
                holder.type = (TextView) convertView.findViewById(R.id.TextType);
                holder.ratingBar = (RatingBar) convertView.findViewById(R.id.RatingBar);
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Place result = results.get(position);
            holder.name.setText(result.getName());
            holder.address.setText(result.getAddress());
            holder.type.setText(result.getType());
            holder.ratingBar.setRating(result.getRating());
            return convertView;

        }


    }

    private void search(String searchText) throws JSONException {
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.GET, "http://83.212.96.164/searchapp/rest/name/?search=" + searchText, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonResponse) {
                        try {
                            JSONArray jsonResults = jsonResponse.getJSONArray("res");
                            dataAdapter.results.clear();
                            for (int i = 0; i < jsonResults.length(); i++) {
                                Place res = new Place();
                                res.setName(jsonResults.getJSONObject(i).getString("name"));
                                res.setId(jsonResults.getJSONObject(i).getString("id"));
                                res.setAddress(jsonResults.getJSONObject(i).getString("formatted_address"));
                                res.setType(jsonResults.getJSONObject(i).getString("types"));
                                res.setRating(jsonResults.getJSONObject(i).getString("rating"));
                                dataAdapter.results.add(res);
                            }
                            dataAdapter.notifyDataSetChanged();
                            dataAdapter.clear();
                            for (int i = 0, l = dataAdapter.results.size(); i < l; i++)
                                dataAdapter.add(dataAdapter.results.get(i));
                            dataAdapter.notifyDataSetInvalidated();
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