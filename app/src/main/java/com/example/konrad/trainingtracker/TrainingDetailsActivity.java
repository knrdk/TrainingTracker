package com.example.konrad.trainingtracker;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.konrad.trainingtracker.datastore.TrainingDBAdapter;
import com.example.konrad.trainingtracker.fragments.TrainingInfoFragment;


public class TrainingDetailsActivity extends ActionBarActivity {
    public final static String INTENT_ARGUMENT_ID = "t_id";
    private TrainingDBAdapter database;

    private Training training = new Training();
    private Duration duration = new Duration();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_details);

        long id = getIntent().getLongExtra(INTENT_ARGUMENT_ID, 0);

        database = new TrainingDBAdapter(this);
        training = database.getTraining(id);
        duration = new Duration(training.getDuration());
        initializeTrainingInfoFragment();
    }

    private void initializeTrainingInfoFragment() {
        TrainingInfoFragment trainingInfoFragment = (TrainingInfoFragment) getFragmentManager().findFragmentById(R.id.training_info);
        trainingInfoFragment.setTraining(training);
        trainingInfoFragment.setDuration(duration);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_training_details, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
