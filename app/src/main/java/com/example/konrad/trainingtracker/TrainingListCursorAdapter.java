package com.example.konrad.trainingtracker;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;


/**
 * Created by Konrad on 2015-02-01.
 */
public class TrainingListCursorAdapter extends CursorAdapter {
    Cursor cursor;

    public TrainingListCursorAdapter(Context context, Cursor cur) {
        super(context, cur);
        cursor = cur;
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return null;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

    }

    public int getCount() {
        // TODO Auto-generated method stub
        return cursor.getCount();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }
}
