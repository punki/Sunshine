package com.example.punki.sunshne.openweathermap.model;

public class City {
    public final String name;
    public final String country;

    public City(String name, String country) {
        this.name = name;
        this.country = country;
    }

    @Override
    public String toString() {
        return "City{" +
                "name='" + name + '\'' +
                ", country='" + country + '\'' +
                '}';
    }
}
