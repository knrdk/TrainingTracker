package com.example.konrad.trainingtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Comarch on 2015-01-30.
 */
public class Segment {
    private LinkedList<SpacetimePoint> points = new LinkedList<>();
    ;
    private double distance = 0;
    private long duration = 0;
    private double currentSpeed = 0;
    private SpacetimePoint lastPoint;

    public void addPoint(SpacetimePoint point) {
        points.add(point);
        if (lastPoint != null) {
            double lastDistance = lastPoint.getDistanceTo(point);
            long lastDuration = lastPoint.getDifferenceInMiliseconds(point);
            distance += lastDistance;
            duration += lastDuration;
            currentSpeed = getSpeed(lastDistance, lastDuration);
        }
        lastPoint = point;
    }

    public LatLng getLastLocation() {
        if (lastPoint != null) {
            return lastPoint.getLocation();
        } else {
            return null;
        }
    }

    public Collection<SpacetimePoint> getPoints() {
        return points;
    }

    public double getDistance() {
        return distance;
    }

    public long getDuration() {
        return duration;
    }

    public double getCurrentSpeed(){
        return currentSpeed;
    }

    private static double getSpeed(double distance, long duration){
        double durationInSeconds = (double)duration/1000;
        if(durationInSeconds>0){
            return distance/durationInSeconds;
        }else{
            return 0;
        }
    }
}
