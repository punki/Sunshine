package com.example.punki.sunshne;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.punki.sunshne.domain.Units;
import com.example.punki.sunshne.openweathermap.OpenWeatherFetchTask;
import com.example.punki.sunshne.storage.WeatherContract;
import com.example.punki.sunshne.mapper.UnitConverterMapper;

import java.util.Date;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter weatherAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initForecastAdapter(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    private void initForecastAdapter(View rootView) {
        weatherAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_forecast,
                null,
                // the column names to use to fill the textviews
                new String[]{
                        WeatherContract.WeatherEntry.COLUMN_DATETEXT,
                        WeatherContract.LocationEntry.COLUMN_CITY_NAME,
                        WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
                        WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
                        WeatherContract.WeatherEntry.COLUMN_MIN_TEMP
                },
                // the textviews to fill with the data pulled from the columns above
                new int[]{
                        R.id.list_item_date_textview,
                        R.id.list_item_location_textview,
                        R.id.list_item_forecast_textview,
                        R.id.list_item_high_textview,
                        R.id.list_item_low_textview
                },
                0
        );

        weatherAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                boolean isMetric = Utility.isMetric(getActivity());
                switch (columnIndex) {
                    case COL_WEATHER_MAX_TEMP:
                    case COL_WEATHER_MIN_TEMP: {
                        // we have to do some formatting and possibly a conversion
                        ((TextView) view).setText(Utility.formatTemperature(
                                cursor.getDouble(columnIndex), isMetric));
                        return true;
                    }
                    case COL_WEATHER_DATE: {
                        String dateString = cursor.getString(columnIndex);
                        TextView dateView = (TextView) view;
                        dateView.setText(Utility.formatDate(dateString));
                        return true;
                    }
                }
                return false;
            }
        });

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherAdapter);
        listView.setOnItemClickListener(buildItemClickListener());
    }

    private AdapterView.OnItemClickListener buildItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = ((SimpleCursorAdapter)parent.getAdapter()).getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    String date = cursor.getString(COL_WEATHER_DATE);
                    String dateString = Utility.formatDate(date);
                    String weatherDescription = cursor.getString(COL_WEATHER_DESC);
                    String location = cursor.getString(COL_CITY_NAME);

                    boolean isMetric = Utility.isMetric(getActivity());
                    String high = Utility.formatTemperature(
                            cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                    String low = Utility.formatTemperature(
                            cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                    String detailString = String.format("%s %s - %s - %s/%s",
                            dateString,location, weatherDescription, high, low);

                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .putExtra(DetailActivity.INTENT_DATE, date);
                    startActivity(intent);
                }
            }
        };
    }

    private void loadDataFromServer() {
        Param ffParam = ((ForecastFragmentParamSupplier) getActivity()).getForecastFragmentParam();

        UnitConverterMapper unitConverterMapper = new UnitConverterMapper(ffParam.units);
        FetchWeatherTask<OpenWeatherFetchTask.Param> fetchWeatherTask = new OpenWeatherFetchTask(
                getActivity().getContentResolver());

        fetchWeatherTask.execute(new OpenWeatherFetchTask.Param(ffParam.location, 11));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_refresh == item.getItemId()) {
            loadDataFromServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    /////////////////loader////////////////////////

    private static final int FORECAST_LOADER = 0;

    private String mLocation;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.LocationEntry.COLUMN_CITY_NAME
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;

    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_CITY_NAME = 6;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        weatherAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        weatherAdapter.swapCursor(null);
    }
    ///////////////////////////////////////////////////

    public static class Param {
        public final String location;
        public final Units units;

        public Param(String location, Units units) {
            this.location = location;
            this.units = units;
        }
    }


    public static interface ForecastFragmentParamSupplier {
        Param getForecastFragmentParam();
    }
}
