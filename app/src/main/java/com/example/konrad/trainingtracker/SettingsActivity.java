package com.example.konrad.trainingtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity {
    public final static float DEFAULT_MINIMAL_ACCURACY = 50.0f;

    private EditText minimalAccuracyET;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        float currentMinimalAccuracy = sharedPref.getFloat(getString(R.string.preference_minimal_accuracy),DEFAULT_MINIMAL_ACCURACY);

        minimalAccuracyET = (EditText) findViewById(R.id.minimalAccuracy);
        minimalAccuracyET.setText(Float.toString(currentMinimalAccuracy));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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

    public void onSaveClick(View view){
        String accuracyString = minimalAccuracyET.getText().toString();
        if(!accuracyString.isEmpty()) {
            float minimalAccuracy = Float.valueOf(accuracyString);

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putFloat(getString(R.string.preference_minimal_accuracy),minimalAccuracy);
            editor.commit();
        }

        finish();
    }
}
