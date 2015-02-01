package com.example.konrad.trainingtracker.maper;

import com.example.konrad.trainingtracker.Training;
import com.example.konrad.trainingtracker.viewmodel.TrainingDetailedInfo;

/**
 * Created by Konrad on 2015-02-01.
 */
public final class TrainingDetailedInfoMaper {
    public static TrainingDetailedInfo Map(Training training){
        return new TrainingDetailedInfo();
    }
}
