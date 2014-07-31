package com.example.punki.sunshne.model;

import com.example.punki.sunshne.openweathermap.model.OpenWeatherResponse;

public class FetchWeatherModel {
    public OpenWeatherResponse weatherResponse;

    public FetchWeatherModel(OpenWeatherResponse weatherResponse) {
        this.weatherResponse = weatherResponse;
    }



    @Override
    public String toString() {
        return "FetchWeatherModel{" +
                "weatherResponse=" + weatherResponse +
                '}';
    }
}
