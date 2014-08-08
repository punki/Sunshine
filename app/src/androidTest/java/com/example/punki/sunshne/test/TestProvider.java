package com.example.punki.sunshne.test;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.punki.sunshne.data.WeatherContract;
import com.example.punki.sunshne.data.WeatherDbHelper;

import java.util.Set;

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

    private void testWeatherCRUD(SQLiteDatabase db, long locationRowId) {
        ContentValues weatherValues = TestProvider.createWeatherValues(locationRowId);
        testInsertWeather(db, weatherValues);
        testSelectAllFromWeather(weatherValues);
    }

    private long testLocationCRUD(SQLiteDatabase db) {
        ContentValues locationValues = TestProvider.createNorthPoleLocationValues();
        long locationRowId = testInsertLocation(db, locationValues);
        testSelectAllFromLocation(locationValues);
        testSelectByIdFromLocation(locationValues, locationRowId);
        return locationRowId;
    }

    private void testSelectByIdFromLocation(ContentValues locationValues, long id) {
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(id),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestProvider.validateCursor(cursor, locationValues);

    }

    private void testSelectAllFromWeather(ContentValues weatherValues) {
        // A cursor is your primary interface to the query results.
        Cursor weatherCursor = mContext.getContentResolver().query(
                WeatherEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestProvider.validateCursor(weatherCursor, weatherValues);
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

        TestProvider.validateCursor(cursor, locationValues);
    }

    private long testInsertLocation(SQLiteDatabase db, ContentValues testValues) {
        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);
        return locationRowId;
    }

    static ContentValues createWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);

        return weatherValues;
    }

    static ContentValues createNorthPoleLocationValues() {
        // Create a new map of values, where column names are the keys
        ContentValues testValues = new ContentValues();
        testValues.put(LocationEntry.COLUMN_LOCATION_SETTING, "99705");
        testValues.put(LocationEntry.COLUMN_CITY_NAME, "North Pole");
        testValues.put(LocationEntry.COLUMN_COORD_LAT, 64.7488);
        testValues.put(LocationEntry.COLUMN_COORD_LONG, -147.353);

        return testValues;
    }

    static void validateCursor(
            Cursor valueCursor, ContentValues expectedValues) {

        // If possible, move to the first row of the query results.
        assertTrue(valueCursor.moveToFirst());

        // get the content values out of the cursor at the current position
        ContentValues resultValues = new ContentValues();
        DatabaseUtils.cursorRowToContentValues(valueCursor, resultValues);

        // make sure the values match the ones we put in
        validateContentValues(resultValues, expectedValues);
        valueCursor.close();
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    static void validateContentValues(ContentValues actual, ContentValues expected) {
        Set<String> expectedKeys = expected.keySet();
        for (String key : expectedKeys) {
            assertTrue(actual.containsKey(key));
            assertTrue(actual.getAsString(key).equals(expected.getAsString(key)));
        }
    }
}