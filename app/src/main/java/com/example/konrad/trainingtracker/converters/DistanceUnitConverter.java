package com.example.konrad.trainingtracker.converters;

/**
 * Created by Comarch on 2015-02-03.
 */
public class DistanceUnitConverter{
    private DistanceUnit targetUnit;

    public DistanceUnitConverter(DistanceUnit targetUnit){
        this.targetUnit = targetUnit;
    }

    public double Convert(double distance){
        return Convert(distance, targetUnit);
    }

    public static double Convert(double distance, DistanceUnit targetUnit){
        switch(targetUnit){
            case M:
                return distance;
            case KM:
                return distance/1000;
            default:
                return distance;
        }
    }
}
