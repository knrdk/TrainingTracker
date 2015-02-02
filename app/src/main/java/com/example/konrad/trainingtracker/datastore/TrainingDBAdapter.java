package com.example.konrad.trainingtracker.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import com.example.konrad.trainingtracker.Segment;
import com.example.konrad.trainingtracker.SpacetimePoint;
import com.example.konrad.trainingtracker.Training;

/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingDBAdapter {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "Training.db";
    private TrainingDbHelper dbHelper;


    public TrainingDBAdapter(Context context){
        dbHelper = new TrainingDbHelper(context);
    }

    public long insertTraining(Training training){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        long id;
        try {
            ContentValues values = new ContentValues();
            values.put(TrainingContract.TrainingEntry.COLUMN_NAME_DISTANCE, training.getDistance());
            values.put(TrainingContract.TrainingEntry.COLUMN_NAME_DURATION, training.getDuration());

            id = db.insert(TrainingContract.TrainingEntry.TABLE_NAME, null, values);

            for(Segment segment: training.getSegments()){
                insertSegment(segment, id, db);
            }

            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }
        return id;
    }

    private void insertSegment(Segment segment, long trainingId, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(TrainingContract.SegmentEntry.COLUMNN_NAME_TRAINING_ID, trainingId);
        long id = db.insert(TrainingContract.SegmentEntry.TABLE_NAME, null, values);

        for(SpacetimePoint point: segment.getPoints()){
            insertPoint(point, id, db);
        }
    }

    private void insertPoint(SpacetimePoint point, long segmentId, SQLiteDatabase db){
        ContentValues values = new ContentValues();
        values.put(TrainingContract.PointEntry.COLUMN_NAME_SEGMENT_ID, segmentId);
        values.put(TrainingContract.PointEntry.COLUMN_NAME_DATE, point.getDate().toString());
        values.put(TrainingContract.PointEntry.COLUMN_NAME_LATITUDE, point.getLocation().latitude);
        values.put(TrainingContract.PointEntry.COLUMN_NAME_LONGITUDE, point.getLocation().longitude);
        values.put(TrainingContract.PointEntry.COLUMN_NAME_ACCURACY, point.getAccuracy());

        db.insert(TrainingContract.PointEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllTrainings(){
        String[] projection = {
                TrainingContract.TrainingEntry.COLUMN_NAME_ID,
                TrainingContract.TrainingEntry.COLUMN_NAME_DISTANCE,
                TrainingContract.TrainingEntry.COLUMN_NAME_DURATION
        };

        String sortOrder = TrainingContract.TrainingEntry.COLUMN_NAME_ID + " DESC";

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        return db.query(
                TrainingContract.TrainingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }

    private class TrainingDbHelper extends SQLiteOpenHelper {
        public TrainingDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TrainingContract.CREATE_TRAINING);
            db.execSQL(TrainingContract.CREATE_SEGMENT);
            db.execSQL(TrainingContract.CREATE_POINT);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(TrainingContract.DELETE_POINT);
            db.execSQL(TrainingContract.DELETE_SEGMENT);
            db.execSQL(TrainingContract.DELETE_TRAINING);
            onCreate(db);
        }
    }
}
