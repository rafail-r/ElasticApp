package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
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
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            public void onItemClick(AdapterView<?> parent, View view,
//                                    int position, long id) {
//                SearchDisplayResult selectedItem = (SearchDisplayResult) parent.getItemAtPosition(position);
//                if (selectedItem.getIsCategoryOrRegion()) {
//                    mTracker.send(new HitBuilders.EventBuilder()
//                            .setCategory("Action")
//                            .setAction("What Search Selected Category")
//                            .build());
//                    searchOptions.setCategoryID(selectedItem.getId());
//                    searchOptions.setCategoryString(selectedItem.getName());
//                    Intent intent = new Intent();
//                    intent.putExtra("searchOptions", searchOptions);
//                    setResult(RESULT_OK, intent);
//                    finish();
//                } else {
//                    mTracker.send(new HitBuilders.EventBuilder()
//                            .setCategory("Action")
//                            .setAction("What Search Selected Place")
//                            .build());
//                    goToDetails(selectedItem);
//
//                }
//
//            }
//        });
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
            TextView code;

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

                convertView = vi.inflate(R.layout.live_result_item, parent, false);
                holder = new ViewHolder();
                holder.code = (TextView) convertView.findViewById(R.id.textView);
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            Place result = results.get(position);
            holder.code.setText(result.getName());

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