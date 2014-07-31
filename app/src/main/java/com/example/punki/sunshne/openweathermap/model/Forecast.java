package com.example.punki.sunshne.openweathermap.model;

import java.util.List;

public class Forecast {
    public final long dt;
    public final Temperature temp;
    public final List<Weather> weather;
    public final double rain;

    public Forecast(long dt, Temperature temp, List<Weather> weather, double rain) {
        this.dt = dt;
        this.temp = temp;
        this.weather = weather;
        this.rain = rain;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "dt=" + dt +
                ", temp=" + temp +
                ", weather=" + weather +
                ", rain=" + rain +
                '}';
    }
}
