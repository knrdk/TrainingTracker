package com.example.konrad.trainingtracker;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.konrad.trainingtracker.datastore.TrainingDBAdapter;
import com.example.konrad.trainingtracker.fragments.GpsReadyFragment;
import com.example.konrad.trainingtracker.fragments.PausedFragment;
import com.example.konrad.trainingtracker.fragments.RunningFragment;
import com.example.konrad.trainingtracker.fragments.TrainingInfoFragment;
import com.example.konrad.trainingtracker.fragments.WaitingForGpsFragment;
import com.example.konrad.trainingtracker.model.Duration;
import com.example.konrad.trainingtracker.model.SpacetimePoint;
import com.example.konrad.trainingtracker.model.Training;
import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends ActionBarActivity implements SpacetimeListener, DurationListener {
    private static final String WAKE_LOCK_TAG = "TrainingTrackerWakeLockTag";
    private static final int NOTIFICATION_DETAIL_ID = 123;
    private PowerManager.WakeLock wakeLock;
    LocationManager locationManager;
    private GpsLocationListener locationListener;

    private NotificationManager notificationManager;

    private TrainingDBAdapter database;

    private boolean isVisible = false;
    private boolean shouldUpdateStateFragment = false;

    private TrackerState state;
    private Training training = new Training();
    private Stopwatch stopwatch;
    private boolean shouldStartStopwatch;
    private Duration duration;

    TrainingInfoFragment trainingInfoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeTrainingInfoFragment();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        initializeWakeLock();
        initializeLocationListener();
        initializeState();

        stopwatch = new Stopwatch(this);
        database = new TrainingDBAdapter(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
        if(shouldUpdateStateFragment){
            updateStateFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }else if(id == R.id.action_history){
            Intent intent = new Intent(MainActivity.this, TrainingListActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeTrainingInfoFragment() {
        trainingInfoFragment = (TrainingInfoFragment) getFragmentManager().findFragmentById(R.id.training_info);
        trainingInfoFragment.setTraining(training);
    }

    private void initializeState() {
        state = TrackerState.WAITING_FOR_GPS;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, new WaitingForGpsFragment()).commit();
    }

    private void initializeWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);
    }

    private void initializeLocationListener() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertGpsDisabled();
        }

        setLastLocation();
        requestLocationUpdates();
    }

    private void setLastLocation(){
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            LatLng lastLatLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            trainingInfoFragment.updateLastLocation(lastLatLng);
        }
    }

    private void requestLocationUpdates(){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        float currentMinimalAccuracy = sharedPref.getFloat(getString(R.string.preference_minimal_accuracy),SettingsActivity.DEFAULT_MINIMAL_ACCURACY);

        locationListener = new GpsLocationListener(this, currentMinimalAccuracy);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1, 0.1f, locationListener);
    }

    private void setState(TrackerState newState) {
        state = newState;

        if(isVisible){
            updateStateFragment();
        }else{
            shouldUpdateStateFragment=true;
        }
    }

    private void updateStateFragment(){
        if (state == TrackerState.WAITING_FOR_GPS) {
            throw new IllegalArgumentException("Can't set WAITING_FOR_GPS state");
        }

        Fragment fragment = null;
        if (state == TrackerState.GPS_READY) {
            fragment = new GpsReadyFragment();
        }else if(state == TrackerState.RUNNING){
            fragment = new RunningFragment();
        }
        else if(state == TrackerState.PAUSED){
            fragment = new PausedFragment();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void gpsReady() {
        if (state == TrackerState.WAITING_FOR_GPS) {
            setState(TrackerState.GPS_READY);
        }
    }

    @Override
    public void addSpacetimePoint(SpacetimePoint x) {
        if (state == TrackerState.RUNNING) {
            if (shouldStartStopwatch) {
                stopwatch.start();
                shouldStartStopwatch = false;
            }

            training.addNewPoint(x);
            trainingInfoFragment.updateTraining();
        }
    }

    public void onStartTraining(View view) {
        if (state == TrackerState.GPS_READY) {
            wakeLock.acquire();
            training.addNewSegment();
            shouldStartStopwatch = true;
            setState(TrackerState.RUNNING);
        }
    }

    public void onPauseTraining(View view) {
        if (state == TrackerState.RUNNING) {
            if (!shouldStartStopwatch) {
                stopwatch.pause();
            } else {
                shouldStartStopwatch = false;
            }
            setState(TrackerState.PAUSED);
        }
    }

    public void onResumeTraining(View view) {
        if (state == TrackerState.PAUSED) {
            training.addNewSegment();
            shouldStartStopwatch = true;
            setState(TrackerState.RUNNING);
        }
    }

    public void onStopTraining(View view) {
        if (state == TrackerState.PAUSED) {
            stopwatch.stop();
            setState(TrackerState.STOPPED);
            wakeLock.release();
            long id = database.insertTraining(training);

            openTrainingDetails(id);
        }
    }

    private void openTrainingDetails(long id){
        Intent intent = new Intent(MainActivity.this,
                TrainingDetailsActivity.class);
        intent.putExtra(TrainingDetailsActivity.INTENT_ARGUMENT_PARENT_ACTIVITY, TrainingDetailsActivity.INTENT_VALUE_MAIN_ACTIVITY);
        intent.putExtra(TrainingDetailsActivity.INTENT_ARGUMENT_ID, id);
        startActivity(intent);

        stop();
    }

    @Override
    public void setDuration(Duration newValue) {
        duration = newValue;
        trainingInfoFragment.setDuration(duration);
        String timerValue = duration.toString();
        showNotification(timerValue);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && state==TrackerState.RUNNING) {
            showAlertCloseApplication();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showAlertCloseApplication() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle(getString(R.string.confirmation));
        alertDialog.setMessage(getString(R.string.wantStop));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.yes),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stop();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showAlertGpsDisabled() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getString(R.string.gpsDisabled))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton(getString(R.string.no),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void showNotification(String timerValue) {
        DecimalFormat df = new DecimalFormat("#.##");
        String distanceText = df.format(training.getDistance());

        Intent notificationIntent = new Intent(MainActivity.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setContentIntent(contentIntent)
                        .setSmallIcon(R.drawable.ic_plusone_small_off_client);
        NotificationCompat.InboxStyle inboxStyle =
                new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.trainingInPrograss));
        inboxStyle.addLine(getString(R.string.time) + timerValue);
        inboxStyle.addLine(getString(R.string.distance) + distanceText);
        mBuilder.setStyle(inboxStyle);

        notificationManager.notify(NOTIFICATION_DETAIL_ID, mBuilder.build());
    }

    public void stop() {
        locationListener.unregister();
        locationManager.removeUpdates(locationListener);
        stopwatch.stop();
        notificationManager.cancel(NOTIFICATION_DETAIL_ID);
        finish();
    }

    private enum TrackerState {
        WAITING_FOR_GPS("Waiting for GPS"),
        GPS_READY("GPS Ready"),
        RUNNING("Running"),
        PAUSED("Paused"),
        STOPPED("Stopped");

        String description;

        private TrackerState(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return description;
        }
    }
}
