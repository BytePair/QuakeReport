package com.example.android.quakereport;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class QueryUSGS extends AsyncTask<Object, Object, Object>{

    private String TAG = QueryUSGS.class.getSimpleName();

    // invoked on the UI thread before the task is executed. This step is normally used to
    // setup the task, for instance by showing a progress bar in the user interface.
    @Override
    protected void onPreExecute() {
        Log.d(TAG, "Inside onPreExecute()...");
        super.onPreExecute();
    }

    // invoked on the background thread immediately after onPreExecute() finishes executing.
    // this step is used to perform background computation that can take a long time.
    @Override
    protected String doInBackground(Object[] params) {
        Log.d(TAG, "Inside doInBackground()...");
        return getJSONData((String) params[0]);
    }

    // this method is used to display any form of progress in the user interface
    // while the background computation is still executing.
    @Override
    protected void onProgressUpdate(Object[] values) {
        Log.d(TAG, "Inside onProgressUpdate()...");
        // super.onProgressUpdate(values);
    }

    //  invoked on the UI thread after the background computation finishes.
    // The result of the background computation is passed to this step as a parameter.
    @Override
    protected void onPostExecute(Object o) {
        Log.d(TAG, "Inside onPostExecute()...");
        // super.onPostExecute(o);
    }


    // get JSON data from the given url
    private String getJSONData(String givenURL) {

        Log.d(TAG, "Inside getJSONData method...");
        Log.d(TAG, "My URL: " + givenURL);

        // create URL object
        URL url = null;
        try {
            url = new URL(givenURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // create HttpURLConnection
        HttpURLConnection urlConnection = null;
        try {
            if (url != null) {
                urlConnection = (HttpURLConnection) url.openConnection();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // read the input stream
        String JSONResults = "";
        try {
            InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                JSONResults += line;
            }
            Log.d(TAG, JSONResults);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return JSONResults;
    }
}










