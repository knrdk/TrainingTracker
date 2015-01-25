package com.example.konrad.trainingtracker;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

/**
 * Created by Konrad on 2015-01-24.
 */
public class SpacetimePoint {
    private Date date;
    private LatLng location;
    private float accuracy;

    public SpacetimePoint(Date date, LatLng location, float accuracy) {
        this.date = date;
        this.location = location;
        this.accuracy = accuracy;
    }

    public SpacetimePoint(LatLng location, float accuracy) {
        this(new Date(),location,accuracy);
    }

    public Date getDate() {
        return date;
    }

    public LatLng getLocation() {
        return location;
    }

    public float getAccuracy() {
        return accuracy;
    }


    public float getDistanceTo(SpacetimePoint other){
        return getDistanceBetween(getLocation(),other.getLocation());
    }

    private static float getDistanceBetween(LatLng x, LatLng y){
        Location a = new Location("");
        a.setLatitude(x.latitude);
        a.setLongitude(x.longitude);

        Location b = new Location("");
        b.setLatitude(y.latitude);
        b.setLongitude(y.longitude);

        return Math.abs(a.distanceTo(b));
    }

    public long getDifferenceInMiliseconds(SpacetimePoint other){
        return getDifferenceInMiliseconds(getDate(),other.getDate());
    }

    private static long getDifferenceInMiliseconds(Date x, Date y){
        return (long)Math.abs(x.getTime()-y.getTime());
    }
}
