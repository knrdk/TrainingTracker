package com.example.konrad.trainingtracker;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by Konrad on 2015-01-25.
 */
public class Segment{
    private LinkedList<SpacetimePoint> points;

    public Segment(){
        points = new LinkedList<>();
    }

    public void addPoint(SpacetimePoint point){
        points.add(point);
    }

    public SpacetimePoint getLastPoint(){
        return points.getLast();
    }

    public Collection<SpacetimePoint> getPoints(){
        return points;
    }

    public double getDistanceInMeters(){
        double distance = 0;
        int size = points.size();
        for(int i=0;i<size-1;i++){
            SpacetimePoint first = points.get(i);
            SpacetimePoint second = points.get(i+1);
            distance += first.getDistanceTo(second);
        }
        return distance;
    }

    public long getDurationInMiliseconds(){
        SpacetimePoint start = points.getFirst();
        SpacetimePoint end = points.getLast();
        return start.getDifferenceInMiliseconds(end);
    }
}
