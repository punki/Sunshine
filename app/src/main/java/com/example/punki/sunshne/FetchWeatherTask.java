package com.example.punki.sunshne;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.punki.sunshne.domain.WeatherModel;
import com.example.punki.sunshne.storage.WeatherContract;
import com.example.punki.sunshne.view.Presenter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.punki.sunshne.storage.WeatherContract.*;

public abstract class FetchWeatherTask<Params> extends AsyncTask<Params, Integer, WeatherModel> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private final Presenter<WeatherModel> presenter;
    private final ContentResolver contentResolver;

    protected FetchWeatherTask(Presenter<WeatherModel> presenter, ContentResolver contentResolver) {
        this.presenter = presenter;
        this.contentResolver = contentResolver;
    }

    @Override
    protected WeatherModel doInBackground(Params... params) {
        Params param = params[0];
        WeatherModel weatherModel = doInBackgroundSpecific(params);
        Log.v(LOG_TAG, "WeatherModel: " + weatherModel);
        save(weatherModel);
        return weatherModel;
    }

    private int save(WeatherModel weatherModel) {
        Uri locationUri =   addLocation(weatherModel);
        return contentResolver.bulkInsert(
                WeatherEntry.CONTENT_URI,
                mapToWeatherContract(locationUri, weatherModel));
    }

    private Uri addLocation(WeatherModel weatherModel) {
        Object[] uniqueQuery = WeatherContract.uniqueQuery(weatherModel);
        Cursor cursor = contentResolver.query(LocationEntry.CONTENT_URI, new String[]{LocationEntry._ID}, (String) uniqueQuery[0], (String[]) uniqueQuery[1], null);
        if (!cursor.moveToNext()) {
            Log.v(LOG_TAG, " there is no location, adding new");
            return contentResolver.insert(LocationEntry.CONTENT_URI, mapToLocationContract(weatherModel));
        }else {
            Log.v(LOG_TAG, " there was location, skipping insert");
            return WeatherContract.LocationEntry.buildLocationUri(cursor.getLong(0));
        }
    }


    @Override
    protected void onPostExecute(WeatherModel weatherModel) {
        presenter.display(weatherModel);
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
