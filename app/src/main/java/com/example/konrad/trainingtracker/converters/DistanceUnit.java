package com.example.konrad.trainingtracker.converters;

/**
 * Created by Comarch on 2015-02-03.
 */
public enum DistanceUnit{
    M("M"),
    KM("KM");

    private String decription;

    private DistanceUnit(String description){
        this.decription = description;
    }

    @Override
    public String toString() {
        return decription;
    }
}
