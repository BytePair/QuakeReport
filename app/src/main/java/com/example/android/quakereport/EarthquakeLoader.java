package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * Loads a list of earthquakes by using an AsyncTask to perform the
 * network request to the given URL.
 */
public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {


    /**
     * Tag for log messages
     */
    private static final String TAG = EarthquakeLoader.class.getName();


    /**
     * Query URL
     */
    private String mUrl;


    /**
     * Constructs a new {@link EarthquakeLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public EarthquakeLoader(Context context, String url) {
        super(context);
        mUrl = url;
        Log.v(TAG, "Default EarthquakeLoader constructor called.");
    }

    /**
     * override the onStartLoading() method to call forceLoad() which is a required step
     * to actually trigger the loadInBackground() method to execute
     */
    @Override
    protected void onStartLoading() {
        forceLoad();
        Log.v(TAG, "Call forceLoad() from onStartLoading().");
    }


    /**
     * This is on a background thread.
     */
    @Override
    public List<Earthquake> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.v(TAG, "loadInBackground() called.");
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Earthquake> earthquakes = QueryUtils.getEarthquakesFromURL(mUrl);
        return earthquakes;
    }
}