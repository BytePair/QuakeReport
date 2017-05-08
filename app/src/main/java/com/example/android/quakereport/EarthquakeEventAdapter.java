package com.example.android.quakereport;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static android.support.v4.app.ActivityCompat.startActivity;


/**
 * Created by JR on 4/26/17.
 */

// have to specify that the array adapter will be taking ArrayList of earthquakeEvents
public class EarthquakeEventAdapter extends ArrayAdapter<Earthquake> {

    // default constructor takes the context and an ArrayList of events
    public EarthquakeEventAdapter(@NonNull Context context, @NonNull ArrayList<Earthquake> events) {
        // resource is 0 (not using it)
        super(context, 0, events);
    }

    // now we override the view setup
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // get the data at this position
        Earthquake event = getItem(position);

        // check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.earthquake_event, parent, false);
        }

        // look up the fields we want to populate
        TextView magnitudeTextView = (TextView) convertView.findViewById(R.id.TextView_Magnitude);
        TextView proxTextView      = (TextView) convertView.findViewById(R.id.TextView_Proximity);
        TextView cityTextView      = (TextView) convertView.findViewById(R.id.TextView_City);
        TextView dateTextView      = (TextView) convertView.findViewById(R.id.TextView_Date);

        // populate the text fields with data from our event objects

        // make sure decimal is only 1 decimal place
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        String decimalString = decimalFormat.format(event.getmMagnitude());
        magnitudeTextView.setText(decimalString);

        // split up location into proximity and city
        String fullLocation = event.getmPlace();

        // find proximity and city
        String proximity;
        String city;

        if (fullLocation.toLowerCase().contains("of")) {
            proximity = fullLocation.substring(0, fullLocation.indexOf("of") + 3);
            city = fullLocation.substring(fullLocation.indexOf("of") + 3, fullLocation.length());
        } else {
            proximity = null;
            city = fullLocation;
        }

        // set the text fields
        if (proximity == null) {
            proxTextView.setVisibility(View.GONE);
        } else {
            proxTextView.setText(proximity);
        }
        cityTextView.setText(city);

        // convert int to proper date format
        Date d = new Date(event.getmDate());
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yy\nh:mm a", Locale.US);
        dateTextView.setText(dateFormat.format(d));

        // Set the proper background color on the magnitude circle.
        // Fetch the background from the TextView, which is a GradientDrawable.
        GradientDrawable magnitudeCircle = (GradientDrawable) magnitudeTextView.getBackground();
        // set the color depending on the magnitude
        magnitudeCircle.setColor(findMagColor(event.getmMagnitude()));

        // return the completed view to be rendered on screen
        return convertView;

    }

    // returns the color based on given magnitude
    int findMagColor(double magnitude) {

        int magResourceId;
        int mag = (int) Math.floor(magnitude);
        switch (mag) {
            case 0:
            case 1:
                magResourceId = R.color.magnitude1;
                break;
            case 2:
                magResourceId = R.color.magnitude2;
                break;
            case 3:
                magResourceId = R.color.magnitude3;
                break;
            case 4:
                magResourceId = R.color.magnitude4;
                break;
            case 5:
                magResourceId = R.color.magnitude5;
                break;
            case 6:
                magResourceId = R.color.magnitude6;
                break;
            case 7:
                magResourceId = R.color.magnitude7;
                break;
            case 8:
                magResourceId = R.color.magnitude8;
                break;
            case 9:
                magResourceId = R.color.magnitude9;
                break;
            default:
                magResourceId = R.color.magnitude10plus;
        }
        return ContextCompat.getColor(getContext(), magResourceId);
    }
}
















