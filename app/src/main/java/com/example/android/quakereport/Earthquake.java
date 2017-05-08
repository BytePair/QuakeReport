package com.example.android.quakereport;

/**
 * Created by JR on 4/26/17.
 */

public class Earthquake {

    /** Member Variables **/

    // description of the event
    private String mTitle;
    // description of the location
    private String mPlace;
    // latitude of epicenter
    private double mLatitude;
    // longitude of epicenter
    private double mLongitude;
    // magnitude of event
    private double mMagnitude;
    // depth of event in kilometers
    private double depth;
    // date of event
    private long mDate;
    // total number of reports to DYFI? system
    private Integer mFelt;
    // link to USGS
    private String mLink;

    /** Contructors **/

    public Earthquake() {}

    public Earthquake(String mTitle, String mPlace, double mLatitude, double mLongitude,
                      double mMagnitude, double depth, Integer mDate, Integer mFelt) {
        this.mTitle = mTitle;
        this.mPlace = mPlace;
        this.mLatitude = mLatitude;
        this.mLongitude = mLongitude;
        this.mMagnitude = mMagnitude;
        this.depth = depth;
        this.mDate = mDate;
        this.mFelt = mFelt;
    }

    /** Getters and Setters **/

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmPlace() {
        return mPlace;
    }

    public void setmPlace(String mPlace) {
        this.mPlace = mPlace;
    }

    public double getmLatitude() {
        return mLatitude;
    }

    public void setmLatitude(double mLatitude) {
        this.mLatitude = mLatitude;
    }

    public double getmLongitude() {
        return mLongitude;
    }

    public void setmLongitude(double mLongitude) {
        this.mLongitude = mLongitude;
    }

    public double getmMagnitude() {
        return mMagnitude;
    }

    public void setmMagnitude(double mMagnitude) {
        this.mMagnitude = mMagnitude;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public long getmDate() {
        return mDate;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }

    public Integer getmFelt() {
        return mFelt;
    }

    public void setmFelt(Integer mFelt) {
        this.mFelt = mFelt;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        mLink = link;
    }
}
