package com.example.konrad.trainingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends Activity implements SpacetimeListener, TimerListener {
    private static final String WAKE_LOCK_TAG = "TrainingTrackerWakeLockTag";
    private PowerManager.WakeLock wakeLock;
    private GoogleMap map;

    private TrackerState state;
    private Training training;

    private TextView totalDistance, totalDuration, averageSpeed, currentSpeedTV, trackerState, timerTextView;

    private Stopwatch stopwatch;
    private NotificationManager notificationManager;

    public MainActivity() {
        state = TrackerState.WAITING_FOR_GPS;
        training = new Training();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        totalDistance = (TextView) findViewById(R.id.totalDistance);
        totalDuration = (TextView) findViewById(R.id.totalDuration);
        currentSpeedTV = (TextView) findViewById(R.id.currentSpeed);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        timerTextView = (TextView) findViewById(R.id.timer);

        trackerState = (TextView) findViewById(R.id.trackerState);
        updateStateDescription();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);

        initLocationListener();


        stopwatch = new Stopwatch(this);
        stopwatch.start();

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void setState(TrackerState newState) {
        state = newState;
        updateStateDescription();
    }

    private void updateStateDescription() {
        trackerState.setText(state.toString());
    }

    private void initLocationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkIfGpsIsAvailable(locationManager);
        initLocation(locationManager);

        GpsLocationListener locationListener = new GpsLocationListener(this);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 5, locationListener); //TODO: delete magic numbers
    }

    private void checkIfGpsIsAvailable(LocationManager locationManager) {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showAlertGpsDisabled();
        }
    }

    private void initLocation(LocationManager locationManager) {
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 15));
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
            training.addNewPoint(x);
            updateMap();
            updateInformation();
        }
    }

    public void onStartTraining(View view) {
        if (state == TrackerState.GPS_READY) {
            wakeLock.acquire();
            training.addNewSegment();
            setState(TrackerState.RUNNING);
        }
    }

    public void onPauseTraining(View view) {
        if (state == TrackerState.RUNNING) {
            setState(TrackerState.PAUSED);
        }
    }

    public void onResumeTraining(View view) {
        if (state == TrackerState.PAUSED) {
            training.addNewSegment();
            setState(TrackerState.RUNNING);
        }
    }

    public void onStopTraining(View view) {
        if (state == TrackerState.PAUSED) {
            setState(TrackerState.STOPPED);
            wakeLock.release();
        }
    }

    private void updateMap() {
        updateLastLocation();
        updateTrack();
    }

    private void updateLastLocation() {
        LatLng last = training.getLastLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
    }

    private void updateTrack() {
        for (Segment segment : training.getSegments()) {
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
            for (SpacetimePoint point : segment.getPoints()) {
                polylineOptions.add(point.getLocation());
            }
            map.addPolyline(polylineOptions);
        }
    }

    private void updateInformation() {
        double distance = training.getDistance();
        totalDistance.setText(Double.toString(distance));

        double duration = training.getDuration();
        totalDuration.setText(Double.toString(duration));

        double currentSpeed = training.getCurrentSpeed();
        currentSpeedTV.setText(Double.toString(currentSpeed));

        double speed = training.getAverageSpeed();
        averageSpeed.setText(Double.toString(speed));
    }

    @Override
    public void updateTime(StopwatchViewModel viewModel) {
        String timerValue = viewModel.toString();
        timerTextView.setText(timerValue);
        showNotification(timerValue);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK /*&& state==TrackerState.RUNNING*/) {
            showAlertCloseApplication();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showAlertCloseApplication() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Czy chcesz przerwać trening");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TAK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stop();
                        finish();
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

    private void showAlertGpsDisabled() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS jest wyłączony, czy chcesz go włączyć ?")
                .setCancelable(false)
                .setPositiveButton("TAK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(callGPSSettingIntent);
                            }
                        });
        alertDialogBuilder.setNegativeButton("NIE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void showNotification(String timerValue) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_plusone_small_off_client)
                        .setContentTitle("Total Time")
                        .setContentText(timerValue);

        notificationManager.notify(123, mBuilder.build());
    }

    public void stop() {
        stopwatch.stop();
        notificationManager.cancel(123);
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
