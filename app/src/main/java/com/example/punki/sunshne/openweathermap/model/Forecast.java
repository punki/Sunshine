package com.example.punki.sunshne.openweathermap.model;

import java.util.List;

public class Forecast {
    public long dt;
    public Temp temp;
    public List<Weather> weather;
    public double rain;

    public Forecast(long dt, Temp temp, List<Weather> weather, double rain) {
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
