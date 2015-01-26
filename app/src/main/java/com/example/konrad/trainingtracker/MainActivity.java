package com.example.konrad.trainingtracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.LinkedList;

import static android.location.LocationManager.GPS_PROVIDER;

public class MainActivity extends Activity implements SpacetimeListener, TimerListener  {
    private static final String WAKE_LOCK_TAG = "TrainingTrackerWakeLockTag";
    private PowerManager.WakeLock wakeLock;
    private GoogleMap map;

    private TrackerState state;
    private Training training;

    TextView totalDistance;
    TextView totalDuration;
    TextView averageSpeed;
    TextView currentSpeed;

    TextView trackerState;

    public MainActivity(){
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
        currentSpeed = (TextView) findViewById(R.id.currentSpeed);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);

        trackerState = (TextView) findViewById(R.id.trackerState);
        if (state == TrackerState.WAITING_FOR_GPS) {
            trackerState.setText("WAITING_FOR_GPS");
        }

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, WAKE_LOCK_TAG);

        GpsLocationListener locationListener = new GpsLocationListener(this);
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(GPS_PROVIDER, 1000, 5, locationListener);

        Handler handler = new Handler();
        Timer timer = new Timer(this, handler);
        handler.post(timer);
        timer.start();
    }

    @Override
    public void gpsReady(){
        if(state==TrackerState.WAITING_FOR_GPS){
            state = TrackerState.GPS_READY;
            trackerState.setText("GPS_READY");
        }
    }

    @Override
    public void addSpacetimePoint(SpacetimePoint x) {
        if(state==TrackerState.RUNNING){
            training.addNewPoint(x);
            updateMap();
            updateInformation();
        }
    }

    public void onStartTraining(View view){
        if(state==TrackerState.GPS_READY){
            wakeLock.acquire();
            training.addNewSegment();
            state = TrackerState.RUNNING;
            trackerState.setText("RUNNING");
        }
    }

    public void onPauseTraining(View view){
        if(state==TrackerState.RUNNING){
            state = TrackerState.PAUSED;
            trackerState.setText("PAUSED");
        }
    }

    public void onResumeTraining(View view){
        if(state==TrackerState.PAUSED){
            training.addNewSegment();
            state = TrackerState.RUNNING;
            trackerState.setText("RUNNING");
        }
    }

    public void onStopTraining(View view){
        if(state == TrackerState.PAUSED){
            state = TrackerState.STOPPED;
            wakeLock.release();
            trackerState.setText("STOPPED");
        }
    }

    private void updateMap(){
        updateLastLocation();
        updateTrack();
    }

    private void updateLastLocation() {
        LatLng last = training.getLastLocation();
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
    }

    private void updateTrack(){
        for(Segment segment : training.getSegments()){
            PolylineOptions polylineOptions = new PolylineOptions().geodesic(true);
            for(SpacetimePoint point : segment.getPoints()){
                polylineOptions.add(point.getLocation());
            }
            map.addPolyline(polylineOptions);
        }
    }

    private void updateInformation(){
        double distance = training.getDistanceInMeters();
        totalDistance.setText(Double.toString(distance));

        double duration = training.getDurationInSeconds();
        totalDuration.setText(Double.toString(duration));

        double speed = training.getAverageSpeed();
        averageSpeed.setText(Double.toString(speed));
    }

    @Override
    public void updateTime(long ms) {
        //trackerState.setText(Long.toString(ms));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK/* && state==TrackerState.RUNNING*/){
            AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
            alertDialog.setTitle("Alert");
            alertDialog.setMessage("Czy chcesz anulować trening");
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "TAK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
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
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private enum TrackerState{
        WAITING_FOR_GPS,
        GPS_READY,
        RUNNING,
        PAUSED,
        STOPPED
    }
}
