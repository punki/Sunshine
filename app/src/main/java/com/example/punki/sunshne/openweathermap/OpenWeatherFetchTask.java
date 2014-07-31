package com.example.punki.sunshne.openweathermap;

import android.net.Uri;
import android.util.Log;

import com.example.punki.sunshne.model.FetchWeatherModel;
import com.example.punki.sunshne.FetchWeatherTask;
import com.example.punki.sunshne.openweathermap.model.OpenWeatherResponse;
import com.google.gson.Gson;

public class OpenWeatherFetchTask extends FetchWeatherTask<OpenWeatherFetchTask.Param> {

    private final String LOG_TAG = OpenWeatherFetchTask.class.getSimpleName();
    private final Gson gson = new Gson();

    @Override
    protected FetchWeatherModel doInBackgroundSpecific(Param... params) {
        Uri uri = buildUri(params[0]);

        String json = readJson(uri);
        Log.i(LOG_TAG, "FetchWeatherTask result: "+json);

        OpenWeatherResponse openWeatherResponse = gson.fromJson(json, OpenWeatherResponse.class);
        Log.v(LOG_TAG, "OpenWeatherResponse: " + openWeatherResponse);

        return new FetchWeatherModel(openWeatherResponse);
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
