package com.example.konrad.trainingtracker;

import com.example.konrad.trainingtracker.model.SpacetimePoint;

/**
 * Created by Konrad on 2015-01-24.
 */
public interface SpacetimeListener {
    public void gpsReady();

    public void addSpacetimePoint(SpacetimePoint x);
}
