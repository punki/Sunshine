package com.example.punki.sunshne.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.punki.sunshne.data.WeatherDbHelper;

import junit.framework.Test;

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

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //location
        long locationRowId = testLocationCRUD(db);

        //weather
        testWeatherCRUD(db, locationRowId);

        dbHelper.close();
    }

    private long testLocationCRUD(SQLiteDatabase db) {
        ContentValues expectedLocationValues = TestDb.createNorthPoleLocationValues();
        long locationRowId = testInsertLocation(db, expectedLocationValues);
        testSelectAllFromLocation(expectedLocationValues);
        testSelectByIdFromLocation(locationRowId, expectedLocationValues);
        return locationRowId;
    }

    private void testWeatherCRUD(SQLiteDatabase db, long locationRowId) {
        ContentValues expectedWeatherValues = TestDb.createWeatherValues(locationRowId);
        testInsertWeather(db, expectedWeatherValues);
        testSelectAllFromWeather(expectedWeatherValues);
        ContentValues expectedWeatherAddLocationValues = TestDb.addAllContentValues(
                expectedWeatherValues, TestDb.createNorthPoleLocationValues());
        testSelectByLocation(expectedWeatherAddLocationValues);
        testSelectByLocationAndDate(expectedWeatherAddLocationValues);
    }

    private void testSelectByLocationAndDate(ContentValues expectedWeatherValues) {
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.buildWeatherLocationWithStartDate(TestDb.LOCATION, TestDb.WEATHER_DATE),
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

    private void testInsertWeather(SQLiteDatabase db, ContentValues weatherValues) {
        long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);
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

    private long testInsertLocation(SQLiteDatabase db, ContentValues testValues) {
        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        return locationRowId;
    }

}