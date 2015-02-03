package com.example.konrad.trainingtracker;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.konrad.trainingtracker.datastore.TrainingContract;
import com.example.konrad.trainingtracker.datastore.TrainingDBAdapter;
import com.example.konrad.trainingtracker.model.Duration;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


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
        TextView dateTV = (TextView) view.findViewById(R.id.date);

        Date date = TrainingDBAdapter.parseDate(cursor.getString(cursor.getColumnIndex(TrainingContract.TrainingEntry.COLUMN_NAME_DATE)));
        double distance = cursor.getDouble(cursor.getColumnIndex(TrainingContract.TrainingEntry.COLUMN_NAME_DISTANCE));
        long duration = cursor.getLong(cursor.getColumnIndex(TrainingContract.TrainingEntry.COLUMN_NAME_DURATION));

        String dateString = formatDate(date);

        distanceTV.setText(formatDouble(distance));
        durationTV.setText((new Duration(duration)).toString());
        dateTV.setText(dateString);
    }

    private String formatDate(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return sdf.format(date);
    }

    private String formatDouble(double x){
        DecimalFormat df = new DecimalFormat("#.##");
        return df.format(x);
    }
}
