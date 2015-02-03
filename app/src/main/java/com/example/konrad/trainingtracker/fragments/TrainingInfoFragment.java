package com.example.konrad.trainingtracker.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.konrad.trainingtracker.converters.DistanceUnit;
import com.example.konrad.trainingtracker.converters.DistanceUnitConverter;
import com.example.konrad.trainingtracker.converters.SpeedUnit;
import com.example.konrad.trainingtracker.model.Duration;
import com.example.konrad.trainingtracker.R;
import com.example.konrad.trainingtracker.model.Segment;
import com.example.konrad.trainingtracker.model.SpacetimePoint;
import com.example.konrad.trainingtracker.model.Training;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;

public class TrainingInfoFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    private static final int MAP_CAMERA_ZOOM = 15;
    private GoogleMap map;
    private TextView totalDistance, totalDuration, averageSpeed, currentSpeedTV;
    private Spinner distanceUnitSpinner;
    private ArrayAdapter<DistanceUnit> distanceUnitAdapter;
    private DistanceUnit selectedDistanceUnit = DistanceUnit.KM;
    private Spinner speedUnitSpinner;
    private ArrayAdapter<SpeedUnit> speedUnitAdapter;
    private SpeedUnit selectedSpeedUnit = SpeedUnit.KMPH;

    private Training training;
    private Duration duration;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
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

        Activity activity = getActivity();
        
        distanceUnitAdapter = new ArrayAdapter<DistanceUnit>(activity, android.R.layout.simple_spinner_item, DistanceUnit.values());
        distanceUnitSpinner = (Spinner) view.findViewById(R.id.distance_unit_spinner);
        distanceUnitSpinner.setAdapter(distanceUnitAdapter);
        distanceUnitSpinner.setSelection(distanceUnitAdapter.getPosition(selectedDistanceUnit), true);
        distanceUnitSpinner.setOnItemSelectedListener(this);
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
        DecimalFormat df = new DecimalFormat("#.##");

        double distance = training.getDistance();
        double convertedDistance = DistanceUnitConverter.Convert(distance, selectedDistanceUnit);
        totalDistance.setText(df.format(convertedDistance));

        double currentSpeed = training.getCurrentSpeed();
        currentSpeedTV.setText(df.format(currentSpeed));

        double speed = training.getAverageSpeed();
        averageSpeed.setText(df.format(speed));
    }

    private void updateDuration() {
        String timerValue = duration.toString();
        totalDuration.setText(timerValue);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.distance_unit_spinner:
                selectedDistanceUnit = distanceUnitAdapter.getItem(position);
                break;
        }
        updateInformation();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}


