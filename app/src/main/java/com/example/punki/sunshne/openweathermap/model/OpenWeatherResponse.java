package com.example.punki.sunshne.openweathermap.model;

import java.util.List;

public class OpenWeatherResponse {
    City city;
    List<Forecast> list;

    public OpenWeatherResponse(City city, List<Forecast> list) {
        this.city = city;
        this.list = list;
    }

    @Override
    public String toString() {
        return "OpenWeatherResponse{" +
                "city=" + city +
                ", forecastList=" + list +
                '}';
    }
}
