package com.example.punki.sunshne;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.example.punki.sunshne.model.WeatherModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class FetchWeatherTask<Params> extends AsyncTask<Params, Integer, WeatherModel> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private final ArrayAdapter<String> arrayAdapter;

    protected FetchWeatherTask(ArrayAdapter<String> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    protected WeatherModel doInBackground(Params... params) {
        WeatherModel weatherModel = doInBackgroundSpecific(params);
        Log.v(LOG_TAG, "WeatherModel: " + weatherModel);
        return weatherModel;
    }

    @Override
    protected void onPostExecute(WeatherModel weatherModel) {
        arrayAdapter.clear();
        arrayAdapter.addAll(format(weatherModel));
    }

    private Collection<String> format(WeatherModel weatherModel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Collection<String> forecasts = new ArrayList<String>(weatherModel.days.size());
        for (WeatherModel.Day day : weatherModel.days) {
            String forecast = dateFormat.format(day.date) +
                    " Weather: " + day.weather +
                    " Temp min: " + day.minTemperature +
                    " max: " + day.maxTemperature +
                    " " + weatherModel.country +
                    ", " + weatherModel.city;
            forecasts.add(forecast);
        }
        return forecasts;

    }

    protected abstract WeatherModel doInBackgroundSpecific(Params... params);

    protected final String readJson(Uri uri) {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String forecastJsonStr = null;

        try {
            String uriAsString = uri.toString();
            Log.v(LOG_TAG, "uri string: " + uriAsString);
            URL url = new URL(uriAsString);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }
            forecastJsonStr = buffer.toString();
            return forecastJsonStr;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }
}
