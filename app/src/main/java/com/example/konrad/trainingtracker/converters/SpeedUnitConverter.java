package com.example.konrad.trainingtracker.converters;

/**
 * Created by Comarch on 2015-02-03.
 */
public class SpeedUnitConverter {
    private SpeedUnit targetUnit;

    public SpeedUnitConverter(SpeedUnit targetUnit){
        this.targetUnit = targetUnit;
    }

    public double Convert(double distance){
        return Convert(distance, targetUnit);
    }

    public static double Convert(double speed, SpeedUnit targetUnit){
        switch(targetUnit){
            case MPS:
                return speed;
            case KMPH:
                return speed*3.6;
            default:
                return speed;
        }
    }
}
