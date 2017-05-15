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

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class EarthquakeActivity extends AppCompatActivity {

    private static final String TAG = EarthquakeActivity.class.getName();
    private final String URL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/significant_month.geojson";

    ListView earthquakeListView;
    ProgressBar progressBar;
    TextView textView;
    Button reloadButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity_list);

        // Find a reference to the {@link ListView} in the layout
        earthquakeListView = (ListView) findViewById(R.id.list);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        reloadButton = (Button) findViewById(R.id.Button_Reload);

        // ASYNC data collection from USGS
        EarthquakeAsyncTask earthquakeAsyncTask = new EarthquakeAsyncTask();
        earthquakeAsyncTask.execute(URL);

    }

    /**
     * Async query to USGS servers to get list of earthquake events
     */
    private class EarthquakeAsyncTask extends AsyncTask<String, Void, ArrayList<Earthquake>> {

        /**
         * This method is invoked on the main UI thread before the
         * background work has been completed
         */
        @Override
        protected void onPreExecute() {
            // hide views
            earthquakeListView.setVisibility(View.GONE);
            reloadButton.setVisibility(View.GONE);

            // show views
            progressBar.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         *
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link Earthquake} object as the result.
         **/
        @Override
        protected ArrayList<Earthquake> doInBackground(String... params) {
            return QueryUtils.getEarthquakesFromURL(params[0]);
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the {@link Earthquake} objects
         * (which was returned from the doInBackground() method) and update the views on the screen.
         **/
        @Override
        protected void onPostExecute(ArrayList<Earthquake> earthquakes) {

            // if no earthquakes found (most likely network error...)
            if (earthquakes.isEmpty()) {
                // hide the spinner
                progressBar.setVisibility(View.GONE);
                // update the error message
                textView.setVisibility(View.VISIBLE);
                textView.setText("Could not find any quakes");
            }
            // otherwise update the ui
            else {
                // hide the spinner
                progressBar.setVisibility(View.GONE);
                // hide the error message
                textView.setVisibility(View.GONE);

                // update the list view with earthquakes
                updateListView(earthquakes);

                // make the list view visible
                earthquakeListView.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Fills in the list view with earthquake events
     */
    private void updateListView(ArrayList<Earthquake> earthquakes) {

        EarthquakeEventAdapter earthquakeEventAdapter = new EarthquakeEventAdapter(this, earthquakes);
        earthquakeListView.setAdapter(earthquakeEventAdapter);

    }
}
