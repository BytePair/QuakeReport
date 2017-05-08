/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getName();
    private final String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity_list);

        // Find a reference to the {@link ListView} in the layout
        final ListView earthquakeListView = (ListView) findViewById(R.id.list);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final TextView textView = (TextView) findViewById(R.id.textView);
        final Button reloadButton = (Button) findViewById(R.id.Button_Reload);

        // ASYNC data collection from USGS
        QueryUSGS queryUSGS = new QueryUSGS() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                Log.d(TAG, "Inside onPreExecute() in main...");

                // hide views
                earthquakeListView.setVisibility(View.GONE);
                reloadButton.setVisibility(View.GONE);

                // show views
                progressBar.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
            }
            // invoked on the UI thread after the background computation finishes.
            // The result of the background computation is passed to this step as a parameter.
            @Override
            protected void onPostExecute(Object o) {
                Log.d(TAG, "Inside onPostExecute() in main...");

                String s = (String) o;
                ArrayList<Earthquake> events = new ArrayList<>();
                events = QueryUtils.extractEarthquakes(s);
                ListView earthquakeListView = (ListView) findViewById(R.id.list);
                final EarthquakeEventAdapter earthquakeEventAdapter = new EarthquakeEventAdapter(getApplicationContext(), events);
                earthquakeListView.setAdapter(earthquakeEventAdapter);

                // set onItemClickListener to link to USGS info page
                earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        // get url from the event
                        String itemURL = earthquakeEventAdapter.getItem(position).getLink();
                        // ensure url has http
                        if (!itemURL.startsWith("http://") && !itemURL.startsWith("https://")) {
                            itemURL = "http://" + itemURL;
                        }
                        final String finalUrl = itemURL;
                        // convert string url into uri to pass to intent
                        Uri eqUri = Uri.parse(finalUrl);
                        // create intent to load the link
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(eqUri);
                        // start the intent
                        startActivity(intent);
                    }
                });

                // hide views
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                if (textView != null) {
                    textView.setVisibility(View.GONE);
                }
                if (reloadButton != null) {
                    reloadButton.setVisibility(View.GONE);
                }

                // show views
                earthquakeListView.setVisibility(View.VISIBLE);

            }
        };
        queryUSGS.execute(URL);

    }
}
