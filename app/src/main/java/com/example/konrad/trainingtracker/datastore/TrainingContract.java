package com.example.konrad.trainingtracker.datastore;

import android.provider.BaseColumns;

import com.example.konrad.trainingtracker.Training;

/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingContract {
    private static final String T_TEXT = " TEXT";
    private static final String T_INTEGER = " INTEGER";
    private static final String T_REAL = " REAL";

    public static final String CREATE_TRAINING =
            "CREATE TABLE " + TrainingEntry.TABLE_NAME + " (" +
                    TrainingEntry.COLUMN_NAME_ID + T_INTEGER + " PRIMARY KEY," +
                    TrainingEntry.COLUMN_NAME_TITLE + T_TEXT + "," +
                    TrainingEntry.COLUMN_NAME_DESCRIPTION + T_TEXT + "," +
                    TrainingEntry.COLUMN_NAME_DATE + T_TEXT + "," +
                    TrainingEntry.COLUMN_NAME_DISTANCE + T_REAL + "," +
                    TrainingEntry.COLUMN_NAME_DURATION + T_REAL + " )";

    public static final String DELETE_TRAINING = "DROP TABLE IF EXISTS " + TrainingEntry.TABLE_NAME;

    public static final String CREATE_SEGMENT =
            "CREATE TABLE " + SegmentEntry.TABLE_NAME + " (" +
                    SegmentEntry.COLUMN_NAME_ID + T_INTEGER + " PRIMARY KEY," +
                    SegmentEntry.COLUMNN_NAME_TRAINING_ID + T_INTEGER + " )";

    public static final String DELETE_SEGMENT = "DROP TABLE IF EXISTS " + SegmentEntry.TABLE_NAME;

    public static final String CREATE_POINT =
            "CREATE TABLE " + PointEntry.TABLE_NAME + " (" +
                    PointEntry.COLUMN_NAME_ID + T_INTEGER + " PRIMARY KEY," +
                    PointEntry.COLUMN_NAME_SEGMENT_ID + T_INTEGER + "," +
                    PointEntry.COLUMN_NAME_DATE + T_TEXT + "," +
                    PointEntry.COLUMN_NAME_LATITUDE + T_REAL + "," +
                    PointEntry.COLUMN_NAME_LONGITUDE + T_REAL + "," +
                    PointEntry.COLUMN_NAME_ACCURACY + T_REAL + " )";

    public static final String DELETE_POINT = "DROP TABLE IF EXISTS " + PointEntry.TABLE_NAME;

    public static abstract class TrainingEntry implements BaseColumns {
        public static final String TABLE_NAME = "training";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_DISTANCE = "distance";
        public static final String COLUMN_NAME_DURATION = "duration";
    }

    public static abstract class SegmentEntry implements BaseColumns {
        public static final String TABLE_NAME = "segment";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMNN_NAME_TRAINING_ID = "training_id";
    }

    public static abstract class PointEntry implements BaseColumns {
        public static final String TABLE_NAME = "point";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_SEGMENT_ID = "segment_id";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
        public static final String COLUMN_NAME_ACCURACY = "accuracy";
    }
}
