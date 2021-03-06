package com.example.punki.sunshne.openweathermap.model;

public class Weather {
    public final String main;
    public final String description;

    public Weather(String main, String description) {
        this.main = main;
        this.description = description;
    }

    @Override
    public String toString() {
        return "Weather{" +
                "main='" + main + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
