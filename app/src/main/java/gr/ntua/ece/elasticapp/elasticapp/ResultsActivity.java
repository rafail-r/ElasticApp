package gr.ntua.ece.elasticapp.elasticapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ResultsActivity extends AppCompatActivity {

    ListView listView;
    ResultsCustomAdapter dataAdapter;
    private ResultsCommunicator resultsCommunicator = new ResultsCommunicator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        listView = (ListView) findViewById(R.id.listView);
        displayListView((String) getIntent().getSerializableExtra("text"));
    }

    private void displayListView(String searchText) {
        dataAdapter = new ResultsCustomAdapter(this, R.layout.result_item, (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE));
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
            String url = "http://83.212.96.164/searchapp/rest/name/?search=" + searchText;
            Double lat = (Double) getIntent().getSerializableExtra("lat");
            Double lon = (Double) getIntent().getSerializableExtra("lon");
            if ((lat != null) && (lon != null)) {
                url = "http://83.212.96.164/searchapp/rest/near/?search=" + searchText + "&lat=" + lat + "&lon=" + lon;
            }
            resultsCommunicator.search(url);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class ResultsCommunicator extends HttpCommunicator {

        public void processResults(JSONObject jsonResponse){
            try {
                JSONArray jsonResults = jsonResponse.getJSONArray("res");
                dataAdapter.clear();
                for (int i = 0; i < jsonResults.length(); i++) {
                    Place res = new Place();
                    res.setName(jsonResults.getJSONObject(i).getString("name"));
                    res.setId(jsonResults.getJSONObject(i).getString("id"));
                    res.setAddress(jsonResults.getJSONObject(i).getString("formatted_address"));
                    String type = jsonResults.getJSONObject(i).getJSONArray("types").getString(0);
                    res.setType(type.substring(0, Math.min(type.length(), 10)));

                    String rating = jsonResults.getJSONObject(i).getString("rating");
                    if (rating.equals("-")) res.setRating("0.0");
                    else res.setRating(rating);
                    dataAdapter.add(res);
                }
                dataAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}