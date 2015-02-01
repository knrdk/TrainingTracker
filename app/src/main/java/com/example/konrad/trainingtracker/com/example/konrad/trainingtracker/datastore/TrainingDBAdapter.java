package com.example.konrad.trainingtracker.com.example.konrad.trainingtracker.datastore;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.konrad.trainingtracker.Training;

/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingDBAdapter {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Training.db";
    private TrainingDbHelper dbHelper;


    public TrainingDBAdapter(Context context){
        dbHelper = new TrainingDbHelper(context);
    }

    public long insertTraining(Training training){
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TrainingContract.TrainingEntry.COLUMN_NAME_DISTANCE, training.getDistance());
        values.put(TrainingContract.TrainingEntry.COLUMN_NAME_DURATION, training.getDuration());

        return db.insert(TrainingContract.TrainingEntry.TABLE_NAME,null,values);
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
