package com.example.punki.sunshne.openweathermap.model;

public class Temp {
    public double min;
    public double max;
    public double day;

    public Temp(double min, double max, double day) {
        this.min = min;
        this.max = max;
        this.day = day;
    }

    @Override
    public String toString() {
        return "Temp{" +
                "min=" + min +
                ", max=" + max +
                ", day=" + day +
                '}';
    }
}
