package com.example.konrad.trainingtracker.converters;

/**
 * Created by Comarch on 2015-02-03.
 */
public enum SpeedUnit{
    MPS("m/s"),
    KMPH("km/h");

    private String description;

    private SpeedUnit(String description){this.description = description;}

    @Override
    public String toString(){return description;}
}
