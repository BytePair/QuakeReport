package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {


    private static final String TAG = QueryUtils.class.getSimpleName();


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a USGS source url.
     */
    public static ArrayList<Earthquake> getEarthquakesFromURL(String urlAsString) {

        // get JSON response from URL
        String response = getJSONData(urlAsString);

        // extract earthquake data from JSON response
        return extractEarthquakes(response);
    }


    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    private static ArrayList<Earthquake> extractEarthquakes(String JSON_DATA) {

        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Earthquake> earthquakes = new ArrayList<>();

        // If the JSON data string is empty, return the empty list
        if (JSON_DATA == null || JSON_DATA.isEmpty()) return earthquakes;

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by the SAMPLE_JSON_RESPONSE string and
            // build up a list of Earthquake objects with the corresponding data.
            JSONObject jsonObject = new JSONObject(JSON_DATA);
            JSONArray jsonArray = jsonObject.getJSONArray("features");

            for (int i = 0; i < jsonArray.length(); i++) {

                // Create eq object to hold details of each event
                Earthquake eq = new Earthquake();

                // Get details of current event
                JSONObject currentEqEvent = jsonArray.getJSONObject(i);
                JSONObject currentEqEventDetails = currentEqEvent.getJSONObject("properties");

                // Populate the earthquake object
                eq.setmMagnitude(currentEqEventDetails.optDouble("mag"));
                eq.setmPlace(currentEqEventDetails.optString("place"));
                eq.setmDate(currentEqEventDetails.optLong("time"));
                eq.setLink(currentEqEventDetails.optString("url", "https://earthquake.usgs.gov/earthquakes/map/"));

                // Add eq to ArrayList to populate ListView
                earthquakes.add(eq);
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
            return null;
        }

        // Return the list of earthquakes
        return earthquakes;

    }


    /**
     * Return a string of json data from a string url
     */
    // get JSON data from the given url
    private static String getJSONData(String givenURL) {

        Log.d(TAG, "Inside getJSONData method...");
        Log.d(TAG, "Attempting to retrieve JSON data from " + givenURL);

        // convert string into URL object
        URL url = createURL(givenURL);

        // make request and return json response
        return makeHttpRequest(url);
    }


    /**
     * Attempt to form a URL from a string
     */
    private static URL createURL(String urlString) {

        URL url;

        // if string is empty return null for URL object
        if (urlString == null || urlString.isEmpty()) return null;

        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        return url;
    }


    /**
     * Make an http request and return the response as a string
     */
    private static String makeHttpRequest(URL url) {

        String JSONResponse = "";

        // if url is null, return early
        if (url == null) return JSONResponse;

        // http connection to handle request
        HttpURLConnection httpURLConnection = null;
        // input stream to read response
        InputStream iStream = null;

        try {
            // set options for httpURLConnection and connect
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(10000);    /* milliseconds */
            httpURLConnection.setConnectTimeout(15000); /* milliseconds */
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();
            // if good response (200) read input stream
            if (httpURLConnection.getResponseCode() == 200) {
                iStream = httpURLConnection.getInputStream();
                JSONResponse = readFromInputStream(iStream);
            }
            // we did not get 200, so report the error
            else {
                Log.e(TAG, "!! Error, response code =  " + String.valueOf(httpURLConnection.getResponseCode()));
            }
        }
        catch (IOException e) {
            Log.e(TAG, "!!! Error makeing HTTP request to " + url.toString() + " !!!", e);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            if (iStream != null) {
                try {
                    iStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "!!! Could not close the input stream !!!", e);
                }
            }
        }
        return JSONResponse;
    }


    /**
     * read string from input stream
     */
    private static String readFromInputStream(InputStream inputStream) throws IOException {

        // string builder to combine strings
        StringBuilder stringBuilder = new StringBuilder();

        // if input stream exists, convert to string
        if (inputStream != null) {

            // create buffered reader for input stream
            BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));

            // keep reading lines from buffered reading until end
            String nextLine = null;
            nextLine = bReader.readLine();
            while (nextLine != null) {
                stringBuilder.append(nextLine);
                nextLine = bReader.readLine();
            }
        }

        // return string builder to string
        return stringBuilder.toString();
    }

}