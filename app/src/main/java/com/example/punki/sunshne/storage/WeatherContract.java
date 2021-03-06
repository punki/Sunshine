package com.example.punki.sunshne.storage;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.content.ContentUris;
import android.content.ContentValues;
import android.net.Uri;
import android.provider.BaseColumns;

import com.example.punki.sunshne.domain.WeatherModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * Defines table and column names for the weather database.
 */
public class WeatherContract {


    public static final String CONTENT_AUTHORITY = "com.example.punki.sunshne";
    public static final Uri BASE_CONTENT_URI= Uri.parse("content://"+CONTENT_AUTHORITY);
    public static final String PATH_WEATHER = "weather";
    public static final String PATH_LOCATION = "location";
    public static final String DB_DATE_FORMAT = "yyyyMMdd";

    public static String getDbDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        return dateFormat.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DB_DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /* Inner class that defines the table contents of the weather table */
    public static final class WeatherEntry implements BaseColumns {

        public static final String TABLE_NAME = "weather";

        // Column with the foreign key into the location table.
        public static final String COLUMN_LOC_KEY = "location_id";
        // Date, stored as Text with format yyyy-MM-dd
        public static final String COLUMN_DATETEXT = "date";
        // Weather id as returned by API, to identify the icon to be used
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Short description and long description of the weather, as provided by API.
        // e.g "clear" vs "sky is clear".
        public static final String COLUMN_SHORT_DESC = "short_desc";

        // Min and max temperatures for the day (stored as floats)
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_HUMIDITY = "humidity";

        // Humidity is stored as a float representing percentage
        public static final String COLUMN_PRESSURE = "pressure";

        // Windspeed is stored as a float representing windspeed  mph
        public static final String COLUMN_WIND_SPEED = "wind";

        // Degrees are meteorological degrees (e.g, 0 is north, 180 is south).  Stored as floats.
        public static final String COLUMN_DEGREES = "degrees";



        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_WEATHER).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_WEATHER;

        public static Uri buildWeatherUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildWeatherLocation(String locationSetting) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).build();
        }

        public static Uri buildWeatherLocationWithStartDate(
                String locationSetting, String startDate) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting)
                    .appendQueryParameter(COLUMN_DATETEXT, startDate).build();
        }

        public static Uri buildWeatherLocationWithDate(String locationSetting, String date) {
            return CONTENT_URI.buildUpon().appendPath(locationSetting).appendPath(date).build();
        }

        public static String getLocationSettingFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getDateFromUri(Uri uri) {
            return uri.getPathSegments().get(2);
        }

        public static String getStartDateFromUri(Uri uri) {
            return uri.getQueryParameter(COLUMN_DATETEXT);
        }
    }


    public static final class LocationEntry implements BaseColumns {
        public static final String TABLE_NAME = "location";

        public static final String COLUMN_CITY_NAME = "city_name";
        public static final String COLUMN_LOCATION_SETTING = "location_setting";
        public static final String COLUMN_COORD_LAT = "coord_lat";
        public static final String COLUMN_COORD_LONG = "coord_longitude";



        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOCATION).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOCATION;

        public static Uri buildLocationUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
        public static long getLocationIdFromUri(Uri uri) {
            return ContentUris.parseId(uri);
        }
    }

    public static ContentValues mapToLocationContract(WeatherModel weatherModel) {
        ContentValues location = new ContentValues();
        location.put(LocationEntry.COLUMN_CITY_NAME, weatherModel.city);
        location.put(LocationEntry.COLUMN_COORD_LAT, "-1");
        location.put(LocationEntry.COLUMN_COORD_LONG, "-1");
        location.put(LocationEntry.COLUMN_LOCATION_SETTING, weatherModel.locationSetting);

        return location;
    }

    public static ContentValues[] mapToWeatherContract(Uri locationUri, WeatherModel weatherModel) {
        List<ContentValues> weathers = new ArrayList<ContentValues>(weatherModel.days.size());
        DecimalFormat decimalFormat=new DecimalFormat("##.00");
        for (WeatherModel.Day day : weatherModel.days) {
            ContentValues w = new ContentValues();
            w.put(WeatherEntry.COLUMN_LOC_KEY,ContentUris.parseId(locationUri));
            w.put(WeatherEntry.COLUMN_SHORT_DESC, day.weather);

            w.put(WeatherEntry.COLUMN_DATETEXT, getDbDateString(day.date));

            w.put(WeatherEntry.COLUMN_MAX_TEMP, decimalFormat.format(day.maxTemperature));
            w.put(WeatherEntry.COLUMN_MIN_TEMP, decimalFormat.format(day.minTemperature));
            w.put(WeatherEntry.COLUMN_DEGREES, "-1");
            w.put(WeatherEntry.COLUMN_HUMIDITY, "-1");
            w.put(WeatherEntry.COLUMN_PRESSURE, "-1");
            w.put(WeatherEntry.COLUMN_WIND_SPEED, "-1");
            w.put(WeatherEntry.COLUMN_WEATHER_ID, "-1");

            weathers.add(w);
        }
        return weathers.toArray(new ContentValues[weathers.size()]);
    }


    public static Object[] uniqueQuery(WeatherModel weatherModel) {
        return new Object[]{LocationEntry.COLUMN_LOCATION_SETTING+"=?", new String[]{weatherModel.locationSetting}};
    }
}