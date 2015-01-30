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
    private SpacetimePoint lastPoint;

    public void addPoint(SpacetimePoint point) {
        points.add(point);
        if (lastPoint != null) {
            distance += lastPoint.getDistanceTo(point);
            duration += lastPoint.getDifferenceInMiliseconds(point);
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
}
