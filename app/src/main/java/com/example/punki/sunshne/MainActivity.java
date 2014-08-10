package com.example.punki.sunshne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.punki.sunshne.domain.Units;


public class MainActivity extends Activity implements ForecastFragment.ForecastFragmentParamSupplier {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "onCreate");
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_view_location) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri geoUri = Uri.parse("geo:0,0").buildUpon()
                    .appendQueryParameter("q", getLocation())
                    .build();
            intent.setData(geoUri);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public ForecastFragment.Param getForecastFragmentParam() {
        String postalCode = getLocation();
        Units units = getUnits();
        return new ForecastFragment.Param(postalCode, units);
    }

    private Units getUnits() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return Units.valueOf(sharedPreferences.getString(getString(R.string.pref_unit_key),
                getString(R.string.pref_unit_default)));
    }

    private String getLocation() {
        return Utility.getPreferredLocation(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v(LOG_TAG, "onResume");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(LOG_TAG, "onDestroy");
    }
}
