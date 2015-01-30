package com.example.konrad.trainingtracker;

import com.google.android.gms.maps.model.LatLng;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Konrad on 2015-01-25.
 */
public class Training {
    private LinkedList<Segment> segments = new LinkedList<>();;
    private Segment currentSegment;
    private double distance = 0;
    private long duration = 0;


    public void addNewSegment(){
        if(currentSegment!=null){
            distance+= currentSegment.getDistance();
            duration+= currentSegment.getDuration();
        }
        currentSegment = new Segment();
        segments.add(currentSegment);
    }

    public Collection<Segment> getSegments(){
        return segments;
    }

    public void addNewPoint(SpacetimePoint point){
        currentSegment.addPoint(point);
    }

    public double getDistance(){
        double currentSegmentDistance =0;
        if(currentSegment!=null){
            currentSegmentDistance = currentSegment.getDistance();
        }
        return distance + currentSegmentDistance;
    }

    public double getDuration(){
        long currentDuration = 0;
        if(currentSegment!=null){
            currentDuration = currentSegment.getDuration();
        }
        return (double)(duration + currentDuration)/1000;
    }

    public double getAverageSpeed(){
        double duration = getDuration();
        if(duration > 0)
            return getDistance()/duration*3.6;
        else
            return 0;
    }

    public LatLng getLastLocation(){
        if(currentSegment!=null){
            return currentSegment.getLastLocation();
        }else{
            return null;
        }
    }
}
