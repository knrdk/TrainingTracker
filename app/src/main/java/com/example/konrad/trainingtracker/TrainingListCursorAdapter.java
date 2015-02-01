package com.example.konrad.trainingtracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.konrad.trainingtracker.datastore.TrainingContract;


/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingListCursorAdapter extends CursorAdapter {

    public TrainingListCursorAdapter(Context context, Cursor cur) {
        super(context, cur);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return layoutInflater.inflate(R.layout.training_list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView distanceTV = (TextView) view.findViewById(R.id.totalDistance);
        TextView durationTV = (TextView) view.findViewById(R.id.totalDuration);

        double distance = cursor.getDouble(cursor.getColumnIndex(TrainingContract.TrainingEntry.COLUMN_NAME_DISTANCE));
        long duration = cursor.getLong(cursor.getColumnIndex(TrainingContract.TrainingEntry.COLUMN_NAME_DURATION));

        distanceTV.setText(Double.toString(distance));
        durationTV.setText(Double.toString(duration));
    }
}
