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
import com.example.konrad.trainingtracker.converters.SpeedUnitConverter;
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
    private TextView totalDistance, totalDuration, averageSpeedTV, currentSpeedTV;

    private Spinner distanceUnitSpinner;
    private ArrayAdapter<DistanceUnit> distanceUnitAdapter;
    private DistanceUnit selectedDistanceUnit = DistanceUnit.KM;

    private Spinner averageSpeedUnitSpinner;
    private ArrayAdapter<SpeedUnit> averageSpeedUnitAdapter;
    private SpeedUnit selectedAverageSpeedUnit = SpeedUnit.KMPH;

    private Spinner currentSpeedUnitSpinner;
    private ArrayAdapter<SpeedUnit> currentSpeedUnitAdapter;
    private SpeedUnit selectedCurrentSpeedUnit = SpeedUnit.KMPH;

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
        averageSpeedTV = (TextView) view.findViewById(R.id.averageSpeed);

        Activity activity = getActivity();
        initializeDistanceUnit(view, activity);
        initializeCurrentSpeedUnit(view,activity);
        initializeAverageSpeedUnit(view,activity);
    }

    private void initializeDistanceUnit(View view, Activity activity) {
        distanceUnitAdapter = new ArrayAdapter<DistanceUnit>(activity, android.R.layout.simple_spinner_item, DistanceUnit.values());
        distanceUnitSpinner = (Spinner) view.findViewById(R.id.distance_unit_spinner);
        distanceUnitSpinner.setAdapter(distanceUnitAdapter);
        distanceUnitSpinner.setSelection(distanceUnitAdapter.getPosition(selectedDistanceUnit), true);
        distanceUnitSpinner.setOnItemSelectedListener(this);
    }

    private void initializeAverageSpeedUnit(View view, Activity activity){
        averageSpeedUnitAdapter = new ArrayAdapter<SpeedUnit>(activity,android.R.layout.simple_spinner_item, SpeedUnit.values());
        averageSpeedUnitSpinner = (Spinner) view.findViewById(R.id.average_speed_unit_spinner);
        averageSpeedUnitSpinner.setAdapter(averageSpeedUnitAdapter);
        averageSpeedUnitSpinner.setSelection(averageSpeedUnitAdapter.getPosition(selectedAverageSpeedUnit),true);
        averageSpeedUnitSpinner.setOnItemSelectedListener(this);
    }

    private void initializeCurrentSpeedUnit(View view, Activity activity){
        currentSpeedUnitAdapter = new ArrayAdapter<SpeedUnit>(activity,android.R.layout.simple_spinner_item, SpeedUnit.values());
        currentSpeedUnitSpinner = (Spinner) view.findViewById(R.id.current_speed_unit_spinner);
        currentSpeedUnitSpinner.setAdapter(currentSpeedUnitAdapter);
        currentSpeedUnitSpinner.setSelection(currentSpeedUnitAdapter.getPosition(selectedCurrentSpeedUnit),true);
        currentSpeedUnitSpinner.setOnItemSelectedListener(this);
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
        double convertedCurrentSpeed = SpeedUnitConverter.Convert(currentSpeed, selectedCurrentSpeedUnit);
        currentSpeedTV.setText(df.format(convertedCurrentSpeed));

        double averageSpeed = training.getAverageSpeed();
        double convertedAverageSpeed = SpeedUnitConverter.Convert(averageSpeed, selectedAverageSpeedUnit);
        averageSpeedTV.setText(df.format(convertedAverageSpeed));
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
            case R.id.average_speed_unit_spinner:
                selectedAverageSpeedUnit = averageSpeedUnitAdapter.getItem(position);
                break;
            case R.id.current_speed_unit_spinner:
                selectedCurrentSpeedUnit = currentSpeedUnitAdapter.getItem(position);
                break;
        }
        updateInformation();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}


