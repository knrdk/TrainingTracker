package com.example.konrad.trainingtracker;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.konrad.trainingtracker.datastore.TrainingContract;
import com.example.konrad.trainingtracker.datastore.TrainingDBAdapter;


public class TrainingListActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
    private TrainingDBAdapter database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_list);

        database = new TrainingDBAdapter(this);
        initTrainingList();
    }

    private void initTrainingList() {
        TrainingListCursorAdapter cursorAdapter = new TrainingListCursorAdapter(this,database.getAllTrainings());
        ListView lv = (ListView) findViewById(R.id.trainings_LV);
        lv.setAdapter(cursorAdapter);
        lv.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(TrainingListActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        openTrainingDetails(id);
    }

    private void openTrainingDetails(long id){
        Intent intent = new Intent(TrainingListActivity.this,
                TrainingDetailsActivity.class);
        intent.putExtra(TrainingDetailsActivity.INTENT_ARGUMENT_PARENT_ACTIVITY,TrainingDetailsActivity.INTENT_VALUE_LIST_ACTIVITY);
        intent.putExtra(TrainingDetailsActivity.INTENT_ARGUMENT_ID,id);
        startActivity(intent);
        finish();
    }
}
