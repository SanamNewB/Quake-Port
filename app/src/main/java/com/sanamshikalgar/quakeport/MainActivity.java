package com.sanamshikalgar.quakeport;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&orderby=time&minmag=3&limit=50";
    private EarthquakeAdapter mEA_CC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find a reference to the {@link ListView} in the layout
        ListView earthquakeListView = (ListView) findViewById(R.id.earthquake_activity_list);

        mEA_CC = new EarthquakeAdapter(this, new ArrayList<EachQuakeInfo>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(mEA_CC);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EachQuakeInfo thisQuakewasClicked = mEA_CC.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(thisQuakewasClicked.getUrl());
                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        QuakeAsyn task = new QuakeAsyn(); // Start the AsyncTask to fetch the earthquake data
        task.execute(USGS_REQUEST_URL);
    }

    private class QuakeAsyn extends AsyncTask<String, Void, List<EachQuakeInfo>> {
        @Override
        protected List<EachQuakeInfo> doInBackground(String... urls) {
            // Perform the HTTP request for earthquake data and process the response.
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            
            List<EachQuakeInfo> result = JSONdata.extractEarthquakes(urls[0]); // this is for accessing the 0th element of any URL so QuakeAsyn can work for any String URL
            return result;
        }

        @Override
        protected void onPostExecute(List<EachQuakeInfo> data) {
            mEA_CC.clear(); // Clear the adapter of previous earthquake data

            if (data != null && !data.isEmpty()) {
                mEA_CC.addAll(data);
            }
        }
    }
}
