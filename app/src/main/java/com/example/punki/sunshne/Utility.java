package com.example.punki.sunshne;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.punki.sunshne.storage.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

public class Utility {
    public static String getPreferredLocation(Activity activity) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        return sharedPreferences.getString(activity.getString(R.string.pref_location_key),
                activity.getString(R.string.pref_location_default));
    }
    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_unit_key),
                context.getString(R.string.pref_unit_default))
                .equals(context.getString(R.string.pref_units_metric));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(String dateString) {
        Date date = WeatherContract.getDateFromDb(dateString);
        return DateFormat.getDateInstance().format(date);
    }

}
