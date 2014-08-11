package com.example.punki.sunshne;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import static com.example.punki.sunshne.storage.WeatherContract.*;

public class DetailActivity extends Activity {

    public static final String DETAIL_FRAGMENT = "detailFragment";
    public static final String INTENT_DATE = "date";
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            startDetailFragment();
        }
    }

    private DetailFragment startDetailFragment() {
        Log.v(LOG_TAG, "startDetailFragment");
        DetailFragment detailFragment = new DetailFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.container, detailFragment, DETAIL_FRAGMENT)
                .commit();
        return detailFragment;
    }

    @Override
    protected void onStart() {
        //chwilowo nie uzywamy moze sie zostawiam jako eksperyment
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        DetailFragment detailFragment =
                (DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
        if (detailFragment == null) {
            startDetailFragment();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


        private static final String LOCATION_KEY = "locationKey";
        private String shareText;

        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_detail, container, false);
        }


        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);
            share(menu.findItem(R.id.action_share));
        }

        private void share(MenuItem item) {
            if (item != null) {
                ShareActionProvider shareActionProvider
                        = (ShareActionProvider) item.getActionProvider();
                shareActionProvider.setShareIntent(createShareIntent());
            }
        }

        private Intent createShareIntent() {
            String shareData = shareText + " #SunshineApp";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
            return shareIntent;
        }


        public String getDateFromIntent() {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(INTENT_DATE)) {
                return intent.getStringExtra(INTENT_DATE);
            }
            throw new IllegalStateException("could not find date in intent");
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            initLocation(savedInstanceState);
            getLoaderManager().initLoader(DETAIL_FORECAST_LOADER, null, this);
            super.onActivityCreated(savedInstanceState);
        }

        private void initLocation(Bundle savedInstanceState) {
            if (savedInstanceState != null && savedInstanceState.containsKey(LOCATION_KEY)) {
                mLocation = savedInstanceState.getString(LOCATION_KEY);
            } else {
                mLocation = Utility.getPreferredLocation(getActivity());
            }
        }

        /////////////////loader////////////////////////

        private static final int DETAIL_FORECAST_LOADER = 0;

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
                WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
                WeatherEntry.COLUMN_DATETEXT,
                WeatherEntry.COLUMN_SHORT_DESC,
                WeatherEntry.COLUMN_MAX_TEMP,
                WeatherEntry.COLUMN_MIN_TEMP,
                LocationEntry.COLUMN_LOCATION_SETTING,
                LocationEntry.COLUMN_CITY_NAME
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
            Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithDate(mLocation, getDateFromIntent());

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    weatherForLocationUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            TextView dateTextView = (TextView) getView().findViewById(R.id.detail_date_textview);
            TextView locationTextView = (TextView) getView().findViewById(R.id.detail_location_textview);
            TextView forecastTextView = (TextView) getView().findViewById(R.id.detail_forecast_textview);
            TextView highTextView = (TextView) getView().findViewById(R.id.detail_high_textview);
            TextView lowTextView = (TextView) getView().findViewById(R.id.detail_low_textview);
            if (cursor.moveToFirst()) {
                String dateString = Utility.formatDate(cursor.getString(COL_WEATHER_DATE));
                String forecaste = cursor.getString(COL_WEATHER_DESC);
                String location = cursor.getString(COL_CITY_NAME);

                boolean isMetric = Utility.isMetric(getActivity());
                String high = Utility.formatTemperature(
                        cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
                String low = Utility.formatTemperature(
                        cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

                dateTextView.setText(dateString);
                forecastTextView.setText(forecaste);
                locationTextView.setText(location);
                highTextView.setText(high);
                lowTextView.setText(low);

                shareText = String.format("%s - %s - %s/%s", dateString, location, high, low);

            } else {
                throw new IllegalArgumentException("cursor is empty");
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            if (mLocation != null) {
                outState.putString(LOCATION_KEY, mLocation);
            }
        }

        ///////////////////////////////////////////////////

    }
}
