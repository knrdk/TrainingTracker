package com.example.konrad.trainingtracker.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseLockedException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Point;
import android.util.Log;

import com.example.konrad.trainingtracker.Segment;
import com.example.konrad.trainingtracker.SpacetimePoint;
import com.example.konrad.trainingtracker.Training;
import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingDBAdapter {
    private static final int DATABASE_VERSION = 7;
    private static final String DATABASE_NAME = "Training.db";
    private TrainingDbHelper dbHelper;

    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS");

    public TrainingDBAdapter(Context context){
        dbHelper = new TrainingDbHelper(context);
    }

    public long insertTraining(Training training){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();

        long id;
        try {
            ContentValues values = new ContentValues();
            values.put(TrainingContract.TrainingEntry.COLUMN_NAME_DATE, formatDate(new Date()));
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
        values.put(TrainingContract.PointEntry.COLUMN_NAME_DATE, formatDate(point.getDate()));
        values.put(TrainingContract.PointEntry.COLUMN_NAME_LATITUDE, point.getLocation().latitude);
        values.put(TrainingContract.PointEntry.COLUMN_NAME_LONGITUDE, point.getLocation().longitude);
        values.put(TrainingContract.PointEntry.COLUMN_NAME_ACCURACY, point.getAccuracy());

        db.insert(TrainingContract.PointEntry.TABLE_NAME, null, values);
    }

    public Cursor getAllTrainings(){
        String[] projection = {
                TrainingContract.TrainingEntry.COLUMN_NAME_ID,
                TrainingContract.TrainingEntry.COLUMN_NAME_DATE,
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

    public Training getTraining(long id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Training training = new Training();

        for(Long segmentId : getSegmentsId(id,db)){
            training.addNewSegment();
            for(SpacetimePoint point : getPointsForSegment(segmentId, db)){
                training.addNewPoint(point);
            }
        }

        return training;
    }

    public void deleteTraining(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = TrainingContract.TrainingEntry.COLUMN_NAME_ID + " = ?";
        String selectionArgs[] = {Long.toString(id)};
        db.delete(TrainingContract.TrainingEntry.TABLE_NAME, selection, selectionArgs);

        for(Long segmentId : getSegmentsId(id, db)){
            deleteSegment(segmentId, db);
        }
    }

    private void deleteSegment(long id, SQLiteDatabase db){
        String selection = TrainingContract.SegmentEntry.COLUMN_NAME_ID + " = ?";
        String selectionArgs[] = {Long.toString(id)};
        db.delete(TrainingContract.SegmentEntry.TABLE_NAME, selection, selectionArgs);

        deletePointsForSegment(id,db);
    }

    private void deletePointsForSegment(long segmentId, SQLiteDatabase db){
        String selection = TrainingContract.PointEntry.COLUMN_NAME_SEGMENT_ID + " = ?";
        String selectionArgs[] = {Long.toString(segmentId)};
        db.delete(TrainingContract.PointEntry.TABLE_NAME, selection, selectionArgs);
    }

    private LinkedList<SpacetimePoint> getPointsForSegment(long id, SQLiteDatabase db){
        String[] projection = {
                TrainingContract.PointEntry.COLUMN_NAME_LATITUDE,
                TrainingContract.PointEntry.COLUMN_NAME_LONGITUDE,
                TrainingContract.PointEntry.COLUMN_NAME_ACCURACY,
                TrainingContract.PointEntry.COLUMN_NAME_DATE
        };
        String selection = TrainingContract.PointEntry.COLUMN_NAME_SEGMENT_ID + " = ?";
        String[] selectionArgs = {Long.toString(id)};
        String sortOrder = TrainingContract.PointEntry.COLUMN_NAME_ID;

        Cursor c = db.query(
                TrainingContract.PointEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        LinkedList<SpacetimePoint> output = new LinkedList<>();

        while(c.moveToNext()){
            double latitude = c.getDouble(c.getColumnIndex(TrainingContract.PointEntry.COLUMN_NAME_LATITUDE));
            double longitude = c.getDouble(c.getColumnIndex(TrainingContract.PointEntry.COLUMN_NAME_LONGITUDE));
            float accuracy = c.getFloat(c.getColumnIndex(TrainingContract.PointEntry.COLUMN_NAME_ACCURACY));
            String dateString = c.getString(c.getColumnIndex(TrainingContract.PointEntry.COLUMN_NAME_DATE));

            Date date = parseDate(dateString);
            LatLng location = new LatLng(latitude,longitude);
            SpacetimePoint point = new SpacetimePoint(date,location,accuracy);

            output.add(point);
        }

        return output;
    }

    private LinkedList<Long> getSegmentsId(long trainingId, SQLiteDatabase db){
        String[] projection = {TrainingContract.SegmentEntry.COLUMN_NAME_ID};
        String selection = TrainingContract.SegmentEntry.COLUMNN_NAME_TRAINING_ID + " = ?";
        String[] selectionArgs = {Long.toString(trainingId)};
        String sortOrder = TrainingContract.SegmentEntry.COLUMN_NAME_ID;

        Cursor c = db.query(
                TrainingContract.SegmentEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        LinkedList<Long> output = new LinkedList<>();

        while(c.moveToNext()){
            Long segmentId = c.getLong(c.getColumnIndex(TrainingContract.SegmentEntry.COLUMN_NAME_ID));
            output.add(segmentId);
        }
        return output;
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

    private static String formatDate(Date date){
        return sdf.format(date);
    }

    public static Date parseDate(String date){
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }
}
