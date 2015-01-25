package com.example.konrad.trainingtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Konrad on 2015-01-25.
 */
public class Training {
    private LinkedList<Segment> segments;

    public Training(){
        segments = new LinkedList<>();
    }

    public void addNewSegment(){
        segments.add(new Segment());
    }

    public Collection<Segment> getSegments(){
        return segments;
    }

    public void addNewPoint(SpacetimePoint point){
        Segment last = segments.getLast();
        last.addPoint(point);
    }

    public double getDistanceInMeters(){
        double distance = 0;
        for(Segment segment : segments){
            distance += segment.getDistanceInMeters();
        }
        return distance;
    }

    public double getDurationInSeconds(){
        long duration = 0;
        for(Segment segment : segments){
            duration += segment.getDurationInMiliseconds();
        }
        return (double)duration/1000;
    }

    public double getAverageSpeed(){
        double duration = getDurationInSeconds();
        if(duration > 0)
            return getDistanceInMeters()/duration;
        else
            return 0;
    }

    public LatLng getLastLocation(){
        Segment lastSegment = segments.getLast();
        SpacetimePoint lastPoint = lastSegment.getLastPoint();
        return lastPoint.getLocation();
    }
}
