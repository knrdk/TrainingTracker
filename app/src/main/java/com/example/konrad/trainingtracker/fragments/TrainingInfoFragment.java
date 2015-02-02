package com.example.konrad.trainingtracker.fragments;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.konrad.trainingtracker.Duration;
import com.example.konrad.trainingtracker.R;
import com.example.konrad.trainingtracker.Segment;
import com.example.konrad.trainingtracker.SpacetimePoint;
import com.example.konrad.trainingtracker.Training;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class TrainingInfoFragment extends Fragment {
    private static final int MAP_CAMERA_ZOOM = 15;
    private GoogleMap map;
    private TextView totalDistance, totalDuration, averageSpeed, currentSpeedTV;

    private Training training = new Training();
    private Duration duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        duration = new Duration();
        training = new Training();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training_info, container, false);
        initializeViewFields(view);
        return view;
    }

    private void initializeViewFields(View view) {
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

        totalDistance = (TextView) view.findViewById(R.id.totalDistance);
        totalDuration = (TextView) view.findViewById(R.id.totalDuration);
        currentSpeedTV = (TextView) view.findViewById(R.id.currentSpeed);
        averageSpeed = (TextView) view.findViewById(R.id.averageSpeed);
    }

    public void setDuration(Duration duration){
        this.duration = duration;
        updateDuration();
    }

    public void setTraining(Training training){
        this.training = training;
        updateTraining();
    }

    public void updateLastLocation(LatLng location) {
        if(location!=null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, MAP_CAMERA_ZOOM));
        }
    }

    public void updateTraining(){
        updateInformation();
        updateMap();
    }

    private void updateMap() {
        updateLastLocation(training.getLastLocation());
        updateTrack();
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

        double currentSpeed = training.getCurrentSpeed();
        currentSpeedTV.setText(Double.toString(currentSpeed));

        double speed = training.getAverageSpeed();
        averageSpeed.setText(Double.toString(speed));
    }

    private void updateDuration() {
        String timerValue = duration.toString();
        totalDuration.setText(timerValue);
    }
}
