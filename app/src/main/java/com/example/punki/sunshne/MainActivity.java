package com.example.punki.sunshne;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.punki.sunshne.model.Units;


public class MainActivity extends Activity implements ForecastFragment.ForecastFragmentParamSupplier {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.pref_location_key),
                    getString(R.string.pref_location_default));
    }


}
