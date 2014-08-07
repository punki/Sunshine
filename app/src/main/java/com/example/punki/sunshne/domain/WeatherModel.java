package com.example.punki.sunshne.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

public class WeatherModel {
    public final String country;
    public final String city;
    public final Collection<Day> days;

    public WeatherModel(String country, String city, Collection<Day> days) {
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
