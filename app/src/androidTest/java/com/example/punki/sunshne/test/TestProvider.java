package com.example.punki.sunshne.test;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.punki.sunshne.data.WeatherContract;
import com.example.punki.sunshne.data.WeatherDbHelper;

import static com.example.punki.sunshne.data.WeatherContract.LocationEntry;
import static com.example.punki.sunshne.data.WeatherContract.WeatherEntry;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);
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

    public void testInsertReadProvider() {
        //location
        long locationRowId = testLocationCRUD();

        //weather
        testWeatherCRUD(locationRowId);
    }

    private long testLocationCRUD() {
        ContentValues expectedLocationValues = TestDb.createNorthPoleLocationValues();
        long locationRowId = testInsertLocation(expectedLocationValues);
        testSelectAllFromLocation(expectedLocationValues);
        testSelectByIdFromLocation(locationRowId, expectedLocationValues);
        testUpdateLocation(locationRowId, expectedLocationValues);
        testDeleteLocation(locationRowId);

        //revert delete
        locationRowId = testInsertLocation(expectedLocationValues);
        return locationRowId;
    }

    private void testDeleteLocation(long locationRowId) {
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

    private void testUpdateLocation(long locationRowId, ContentValues expectedLocationValues) {
        ContentValues updValue = new ContentValues(expectedLocationValues);
        updValue.put(LocationEntry._ID,locationRowId);
        updValue.put(LocationEntry.COLUMN_CITY_NAME,"upd city name");
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

    private void testWeatherCRUD(long locationRowId) {
        ContentValues expectedWeatherValues = TestDb.createWeatherValues(locationRowId);
        testInsertWeather(expectedWeatherValues);
        testSelectAllFromWeather(expectedWeatherValues);
        ContentValues expectedWeatherAddLocationValues = TestDb.addAllContentValues(
                expectedWeatherValues, TestDb.createNorthPoleLocationValues());
        testSelectByLocation(expectedWeatherAddLocationValues);
        testSelectByLocationAndStartDate(expectedWeatherAddLocationValues);
        testSelectByLocationAndDate(expectedWeatherAddLocationValues);
    }

    private void testSelectByLocationAndStartDate(ContentValues expectedWeatherValues) {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TestDb.LOCATION, TestDb.WEATHER_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherValues);
    }

    private void testSelectByLocationAndDate(ContentValues expectedWeatherValues) {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithDate(TestDb.LOCATION, TestDb.WEATHER_DATE),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherValues);
    }

    private void testSelectByLocation(ContentValues expectedWeatherValues) {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocation(TestDb.LOCATION),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(weatherCursor, expectedWeatherValues);
    }

    private void testSelectByIdFromLocation(long id, ContentValues locationValues) {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(id),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(cursor, locationValues);

    }

    private void testSelectAllFromWeather(ContentValues expectedWeatherValues) {
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

    private void testInsertWeather(ContentValues weatherValues) {
        Uri insertUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
        long weatherRowId = ContentUris.parseId(insertUri);
        assertTrue(weatherRowId != -1);
    }

    private void testSelectAllFromLocation(ContentValues locationValues) {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestDb.validateCursor(cursor, locationValues);
    }

    private long testInsertLocation(ContentValues testValues) {
        Uri insertUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(insertUri);
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        return locationRowId;
    }

}