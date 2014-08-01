package com.example.punki.sunshne.openweathermap;

import android.net.Uri;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.punki.sunshne.model.WeatherModel;
import com.example.punki.sunshne.FetchWeatherTask;
import com.example.punki.sunshne.openweathermap.model.City;
import com.example.punki.sunshne.openweathermap.model.Forecast;
import com.example.punki.sunshne.openweathermap.model.OpenWeatherResponse;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class OpenWeatherFetchTask extends FetchWeatherTask<OpenWeatherFetchTask.Param> {

    public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private final String LOG_TAG = OpenWeatherFetchTask.class.getSimpleName();
    private final Gson gson = new Gson();

    public OpenWeatherFetchTask(ArrayAdapter<String> arrayAdapter) {
        super(arrayAdapter);
    }

    @Override
    protected WeatherModel doInBackgroundSpecific(Param... params) {
        Uri uri = buildUri(params[0]);

        String json = readJson(uri);
        Log.i(LOG_TAG, "FetchWeatherTask result: "+json);
        if (json == null) {
            return new WeatherModel("Error","", Collections.<WeatherModel.Day>emptyList());
        }

        OpenWeatherResponse openWeatherResponse = gson.fromJson(json, OpenWeatherResponse.class);
        Log.v(LOG_TAG, "OpenWeatherResponse: " + openWeatherResponse);

        return buildWeatherModel(openWeatherResponse);
    }

    private WeatherModel buildWeatherModel(OpenWeatherResponse response) {
        Collection<Forecast> forecasts = response.list;
        Collection<WeatherModel.Day> days = new ArrayList<WeatherModel.Day>(forecasts.size());
        for (Forecast f : forecasts) {
            Calendar calendar = Calendar.getInstance(GMT);
            calendar.setTimeInMillis(f.dt * 1000);
            WeatherModel.Day day=new WeatherModel.Day(
                    calendar.getTime(),
                    f.temp.min,
                    f.temp.max,
                    f.weather.get(0).main);
            days.add(day);
        }

        City city = response.city;
        return new WeatherModel(city.country, city.name, days);
    }

    private Uri buildUri(Param param) {
        Uri.Builder uriBuilder = new Uri.Builder();
        return uriBuilder
                .scheme("http")
                .appendEncodedPath("/api.openweathermap.org/data/2.5/forecast/daily")
                .appendQueryParameter("q", String.valueOf(param.postcode))
                .appendQueryParameter("cnt", String.valueOf(param.limit))
                .appendQueryParameter("mode", "json")
                .appendQueryParameter("units", "metric").build();
    }

    public static class Param {
        public final int postcode;
        public final int limit;

        public Param(int postcode, int limit) {

            this.postcode = postcode;
            this.limit = limit;
        }
    }
}
