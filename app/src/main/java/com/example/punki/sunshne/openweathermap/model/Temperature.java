package com.example.punki.sunshne.openweathermap.model;

public class Temperature {
    public final double min;
    public final double max;
    public final double day;

    public Temperature(double min, double max, double day) {
        this.min = min;
        this.max = max;
        this.day = day;
    }

    @Override
    public String toString() {
        return "Temperature{" +
                "min=" + min +
                ", max=" + max +
                ", day=" + day +
                '}';
    }
}
