package com.example.punki.sunshne.mapper;

import com.example.punki.sunshne.model.Units;
import com.example.punki.sunshne.model.WeatherModel;

import java.util.ArrayList;
import java.util.Collection;


public class UnitConverterMapper implements Mapper<WeatherModel> {

    private final Units targetUnits;

    public UnitConverterMapper(Units targetUnits) {
        this.targetUnits = targetUnits;
    }

    @Override
    public WeatherModel map(WeatherModel data) {
        if (data != null) {
            return new WeatherModel(data.country,data.city,map(data.days));
        }
        return null;
    }

    private Collection<WeatherModel.Day> map(Collection<WeatherModel.Day> days) {
        Collection<WeatherModel.Day> newDays=new ArrayList<WeatherModel.Day>(days.size());
        for (WeatherModel.Day day : days) {
            WeatherModel.Day newDay=new WeatherModel.Day(
                    day.date,
                    convertTemperature(day.minTemperature),
                    convertTemperature(day.maxTemperature),
                    day.weather);
            newDays.add(newDay);
        }
        return newDays;
    }

    private double convertTemperature(double temperature) {
        if (targetUnits == Units.imperial) {
            return (temperature * 1.8) + 32;
        }
        return temperature;
    }
}
