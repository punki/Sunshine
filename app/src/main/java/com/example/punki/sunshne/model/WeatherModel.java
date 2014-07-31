package com.example.punki.sunshne.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WeatherModel {
    public final String country;
    public final String city;
    public final List<Day> days;

    public WeatherModel(String country, String city, List<Day> days) {
        this.country = country;
        this.city = city;
        this.days = new ArrayList<Day>(days);
    }


    @Override
    public String toString() {
        return "WeatherModel{" +
                "days=" + days +
                '}';
    }

    public static class Day {
        public final Date date;
        public final double minTemperature;
        public final double maxTemperature;
        public final String weather;

        public Day(Date date, double minTemperature, double maxTemperature, String weather) {
            this.date = date;
            this.minTemperature = minTemperature;
            this.maxTemperature = maxTemperature;
            this.weather = weather;
        }
    }
}
