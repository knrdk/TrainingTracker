package com.example.konrad.trainingtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.example.konrad.trainingtracker.datastore.TrainingDBAdapter;
import com.example.konrad.trainingtracker.fragments.TrainingInfoFragment;


public class TrainingDetailsActivity extends ActionBarActivity {
    public final static String INTENT_ARGUMENT_ID = "t_id";
    public final static String INTENT_ARGUMENT_PARENT_ACTIVITY = "parent_activity";
    public final static String INTENT_VALUE_MAIN_ACTIVITY = "MainActivity";
    public final static String INTENT_VALUE_LIST_ACTIVITY = "ListActivity";

    private TrainingDBAdapter database;

    private String parentActivity;
    private long trainingId;
    private Training training;
    private Duration duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_training_details);

        parentActivity = getIntent().getStringExtra(INTENT_ARGUMENT_PARENT_ACTIVITY);
        trainingId = getIntent().getLongExtra(INTENT_ARGUMENT_ID, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();

        database = new TrainingDBAdapter(this);
        training = database.getTraining(trainingId);
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
        }else if(id == R.id.action_delete){
            showConfirmationAlert();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && INTENT_VALUE_MAIN_ACTIVITY.equals(parentActivity)) {
            Intent intent = new Intent(TrainingDetailsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }else if(keyCode == KeyEvent.KEYCODE_BACK && INTENT_VALUE_LIST_ACTIVITY.equals(parentActivity)){
            Intent intent = new Intent(TrainingDetailsActivity.this, TrainingListActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showConfirmationAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(TrainingDetailsActivity.this).create();
        alertDialog.setTitle("Potwierdzenie");
        alertDialog.setMessage("Czy chcesz usunąć trening");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TAK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteTraining();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NIE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void deleteTraining(){
        database.deleteTraining(trainingId);

        Intent intent;
        if(INTENT_VALUE_MAIN_ACTIVITY.equals(parentActivity)){
            intent = new Intent(TrainingDetailsActivity.this, MainActivity.class);
        }else {
            intent = new Intent(TrainingDetailsActivity.this, TrainingListActivity.class);
        }

        startActivity(intent);
        finish();
    }
}
