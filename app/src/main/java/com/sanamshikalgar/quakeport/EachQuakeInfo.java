package com.sanamshikalgar.quakeport;

public class EachQuakeInfo {
    private double mMagnitude;
    private String mCity;
    //private String mMMDDYYY;
    private long mTimeInMilliseconds;
    private String mUrl;

    public EachQuakeInfo (double magnitude, String city, long timeInMilliseconds, String url) {
        mMagnitude = magnitude;
        mCity = city;
        mTimeInMilliseconds = timeInMilliseconds;
        mUrl = url;
    }
    // Since the variables are private we need to define public methods so other classes can access these
    public double  getMagnitude() {
        return mMagnitude;
    }

    public String getCity() {
        return mCity;
    }

    public long getTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public String getUrl() {
        return mUrl;
    }
}
