package com.example.punki.sunshne.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.punki.sunshne.data.WeatherDbHelper;

import static com.example.punki.sunshne.data.WeatherContract.LocationEntry;
import static com.example.punki.sunshne.data.WeatherContract.WeatherEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    private long locationRowId;
    private ContentValues expectedLocationValues;
    private ContentValues expectedWeatherValues;
    private ContentValues expectedWeatherAddLocationValues;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        clearDB();

        expectedLocationValues = TestDb.createNorthPoleLocationValues();
        locationRowId = testInsertLocation();
        expectedWeatherValues = TestDb.createWeatherValues(locationRowId);
        expectedWeatherAddLocationValues = TestDb.addAllContentValues(
                expectedWeatherValues, TestDb.createNorthPoleLocationValues());

        testInsertWeather();
    }

    private void clearDB() {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        db.close();
        mContext.getContentResolver().delete(WeatherEntry.CONTENT_URI, null, null);
        mContext.getContentResolver().delete(LocationEntry.CONTENT_URI, null, null);
    }

    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testDeleteLocation() {
        int numberOfDeleted = mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                LocationEntry._ID + " = ?",
                new String[]{String.valueOf(locationRowId)});
        assertEquals(1, numberOfDeleted);

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        assertFalse(cursor.moveToNext());
    }

    public void testUpdateLocation() {
        ContentValues updValue = new ContentValues(expectedLocationValues);
        updValue.put(LocationEntry._ID, locationRowId);
        updValue.put(LocationEntry.COLUMN_CITY_NAME, "upd city name");
        int numberOfUpdated = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI,
                updValue, LocationEntry._ID + " = ? ",
                new String[]{String.valueOf(locationRowId)});
        assertEquals(1, numberOfUpdated);

        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        TestDb.validateCursor(cursor, updValue);
    }

    public void testSelectByLocationAndStartDate() {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TestDb.LOCATION, TestDb.WEATHER_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherAddLocationValues);
    }

    public void testSelectByLocationAndDate() {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestDb.LOCATION, TestDb.WEATHER_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherValues);
    }

    public void testSelectByLocation() {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestDb.LOCATION),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherAddLocationValues);
    }

    public void testSelectByIdFromLocation() {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(cursor, expectedLocationValues);

    }

    public void testSelectAllFromWeather() {
        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherValues);
    }

    public void testInsertWeather() {
        Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, expectedWeatherValues);
        long weatherRowId = ContentUris.parseId(insertUri);
        assertTrue(weatherRowId != -1);
    }

    public void testSelectAllFromLocation() {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(cursor, expectedLocationValues);
    }

    public long testInsertLocation() {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );
        assertFalse(cursor.moveToNext());
        cursor.close();

        Uri insertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, expectedLocationValues);
        long locationRowId = ContentUris.parseId(insertUri);
        assertTrue(locationRowId != -1);
        return locationRowId;
    }

}