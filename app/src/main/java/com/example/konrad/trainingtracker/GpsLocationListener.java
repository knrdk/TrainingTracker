package com.example.konrad.trainingtracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Konrad on 2015-01-24.
 */
public class GpsLocationListener implements LocationListener {
    private boolean isGpsFixed = false;
    private float minimalAccuracyInMeters = 10.0f;
    private float minimalChangeInMeters = 5.0f;
    private int minimalIntervalInMiliseconds = 2000;
    private SpacetimeListener observer;
    private SpacetimePoint lastReturnedPoint = null;

    public GpsLocationListener(SpacetimeListener x){
        observer = x;
    }

    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        Log.i("accuracy", Float.toString(accuracy));
        if(!isGpsFixed){
            if(accuracy<minimalAccuracyInMeters){
                isGpsFixed = true;
                observer.gpsReady();
            }
        }

        if(isGpsFixed){
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            SpacetimePoint current = new SpacetimePoint(currentLocation, accuracy);
            if(shouldNotify(current)){
                lastReturnedPoint = current;
                observer.addSpacetimePoint(current);
            }
        }
    }

    private boolean shouldNotify(SpacetimePoint current){
        if(lastReturnedPoint==null){
            return true;
        }

        if(lastReturnedPoint.getDifferenceInMiliseconds(current) < minimalIntervalInMiliseconds){
            return false;
        }

        if(lastReturnedPoint.getDistanceTo(current) < minimalChangeInMeters){
            return false;
        }

        return true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
