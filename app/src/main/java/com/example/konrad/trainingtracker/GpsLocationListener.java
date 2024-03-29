package com.example.konrad.trainingtracker;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.example.konrad.trainingtracker.model.SpacetimePoint;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Konrad on 2015-01-24.
 */
public class GpsLocationListener implements LocationListener {
    private boolean isGpsFixed = false;
    private float minimalAccuracyInMeters = 50.0f;
    private float minimalChangeInMeters = 0.0f;
    private int minimalIntervalInMiliseconds = 2000;
    private SpacetimeListener observer;
    private SpacetimePoint lastReturnedPoint = null;

    public GpsLocationListener(SpacetimeListener x, float minimalAccuracy) {
        minimalAccuracyInMeters = minimalAccuracy;
        observer = x;
    }

    public void unregister(){
        observer = null;
    }

    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        Log.i("accuracy", Float.toString(accuracy));
        if (!isGpsFixed) {
            if (accuracy < minimalAccuracyInMeters) {
                isGpsFixed = true;
                observer.gpsReady();
            }
        }

        if (observer != null && isGpsFixed) {
            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
            SpacetimePoint current = new SpacetimePoint(currentLocation, accuracy);
            if (shouldNotify(current)) {
                lastReturnedPoint = current;
                observer.addSpacetimePoint(current);
            }
        }
    }

    private boolean shouldNotify(SpacetimePoint current) {
        if (lastReturnedPoint == null) {
            return true;
        }

        if (lastReturnedPoint.getDifferenceInMiliseconds(current) < minimalIntervalInMiliseconds) {
            return false;
        }

        if (lastReturnedPoint.getDistanceTo(current) < minimalChangeInMeters) {
            return false;
        }

        return true;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d("GpsLocationListener", "onStatusChanged");
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d("GpsLocationListener", "onProviderEnabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d("GpsLocationListener", "onProviderDisabled");
    }
}
