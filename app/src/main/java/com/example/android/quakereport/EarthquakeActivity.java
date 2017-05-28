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

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. Need to say the EarthquakeActivity implements the LoaderCallbacks interface and specify what
 * it will return
 */
public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {


    private static final String TAG = EarthquakeActivity.class.getName();

    /**
     * 2. Specify constant ID for the loading in case we are using multiple loaders in the same
     * activity. Can select any integer value, in this case 1
     */
    private static final int EARTHQUAKE_LOADER_ID = 1;


    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";


    /**
     * UI Elements
     */
    private ListView earthquakeListView;
    private ProgressBar progressBar;
    private TextView textView;
    private Button reloadButton;

    /**
     * Adapter for the list of earthquakes
     */
    private EarthquakeEventAdapter earthquakeEventAdapter;


    /**
     * These two methods are to inflate the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * onCreate method
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity_list);

        // Find a reference to the views in the layout
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.textView);
        earthquakeListView = (ListView) findViewById(R.id.list);

        reloadButton = (Button) findViewById(R.id.Button_Reload);
        if (reloadButton != null) {
            reloadButton.setVisibility(View.GONE);
        }

        // Create a new adapter that takes an empty list of earthquakes as input
        earthquakeEventAdapter = new EarthquakeEventAdapter(this, new ArrayList<Earthquake>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        earthquakeListView.setAdapter(earthquakeEventAdapter);

        // Set the empty state textView
        earthquakeListView.setEmptyView(findViewById(R.id.textView));

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected earthquake.
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Find the current earthquake that was clicked on
                Earthquake currentEarthquake = earthquakeEventAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri earthquakeUri = Uri.parse(currentEarthquake.getLink());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });

        /**
         * 4. Within the onCreate(), get the loader manager and tell the loader manager to
         *    initialize the loader with the specified ID, the second argument could be a bundle of
         *    addition information (skipped for now). The third argument is what object should
         *    receive the LoaderCallbacks (and the data when it is finished loading), which will
         *    be this activity.
         */

        // get a reference to the LoaderManager, in order to interact with loaders.
        LoaderManager loaderManager = getLoaderManager();

        // initialize the loader. Pass in the int ID constant defined above and pass in null for
        // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
        // because this activity implements the LoaderCallbacks interface).
        loaderManager.initLoader(EARTHQUAKE_LOADER_ID, null, this);

    }


    /**
     * 3. Override the 3 methods specified in the LoaderCallbacks interface.
     *          - OnCreateLoader()
     *          - OnLoadFinished()
     *          - OnLoaderReset()
     */

    /**
     * OnCreateLoader() for when the LoaderManager has determined that the loader with our specified
     * ID isn't running, so we need to make a new one
     *
     * @return {@link EarthquakeLoader}
     */
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        // get minimum magnitude from shared preferences
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default)
        );

        // get order by from shared preferences
        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", "20");
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }


    /**
     * OnLoadFinished() uses the earthquake data to update our UI
     * by updating the earthquake list in the adapter
     */
    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {

        Log.v(TAG, "onLoadFinished() called.");

        textView.setVisibility(View.GONE);

        // clear the adapter of previous earthquake data
        earthquakeEventAdapter.clear();

        // call display earthquakes to update the UI
        displayEarthquakes(earthquakes);
    }


    /**
     * onLoaderReset() when the loader is reset
     */
    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {

        Log.v(TAG, "onLoaderReset() called.");

        // loader reset, so we can clear out our existing data.
        earthquakeEventAdapter.clear();

    }


    /**
     * displayEarthquakes() will update the array with most recent earthquake data
     *
     * @param earthquakes
     */
    private void displayEarthquakes(List<Earthquake> earthquakes) {

        if (earthquakes == null || earthquakes.isEmpty()) {

            // if we have a good internet connection
            if (CheckInternetStatus(this)) {
                // hide loading bar
                progressBar.setVisibility(View.INVISIBLE);
                // hide reload button
                reloadButton.setVisibility(View.GONE);
                // hide list
                earthquakeListView.setVisibility(View.GONE);
                // update and show status
                Log.v(TAG, "No earthquakes found.");
                textView.setText("No earthquakes found.");
                earthquakeListView.getEmptyView().setVisibility(View.VISIBLE);
            }

            // otherwise let user know we have bad connection
            else {
                displayBadConnection();
            }

        } else {
            // hide loading bar
            progressBar.setVisibility(View.GONE);
            // hide reload button
            reloadButton.setVisibility(View.GONE);
            // hide status
            textView.setVisibility(View.GONE);

            // If there is a valid list of {@link Earthquake}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            earthquakeEventAdapter.addAll(earthquakes);
            earthquakeListView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * displayBadConnection() updates the UI to inform the user they have no internet connection
     */
    private void displayBadConnection() {
        // hide loading bar
        progressBar.setVisibility(View.INVISIBLE);
        // hide reload button
        reloadButton.setVisibility(View.GONE);
        // hide list
        earthquakeListView.setVisibility(View.GONE);
        // update and show status
        Log.v(TAG, "No internet connection.");
        textView.setText("No internet connection.");
        earthquakeListView.getEmptyView().setVisibility(View.VISIBLE);
    }


    /**
     * Returns the status of the internet connection
     *
     * @return True:  Good internet connection False: Bad internet connection
     */
    private boolean CheckInternetStatus(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}