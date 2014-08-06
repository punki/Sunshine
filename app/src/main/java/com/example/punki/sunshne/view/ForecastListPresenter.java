package com.example.punki.sunshne.view;

import android.widget.ArrayAdapter;

import com.example.punki.sunshne.mapper.Mapper;
import com.example.punki.sunshne.mapper.UnitConverterMapper;
import com.example.punki.sunshne.model.WeatherModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class ForecastListPresenter implements Presenter<WeatherModel> {
    private final ArrayAdapter<String> arrayAdapter;
    private Mapper<WeatherModel> mapper;

    public ForecastListPresenter(ArrayAdapter<String> arrayAdapter, Mapper<WeatherModel> mapper) {
        this.arrayAdapter = arrayAdapter;
        this.mapper = mapper;
    }

    @Override
    public void display(WeatherModel weatherModel) {
        arrayAdapter.clear();
        if (weatherModel != null) {
            arrayAdapter.addAll(format(mapper.map(weatherModel)));
        }
    }

    private Collection<String> format(WeatherModel weatherModel) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        DecimalFormat decimalFormat=new DecimalFormat("##.00");
        Collection<String> forecasts = new ArrayList<String>(weatherModel.days.size());
        for (WeatherModel.Day day : weatherModel.days) {
            String forecast = dateFormat.format(day.date) +
                    " Weather: " + day.weather +
                    " Temp min: " + decimalFormat.format(day.minTemperature) +
                    " max: " + decimalFormat.format(day.maxTemperature) +
                    " " + weatherModel.country +
                    ", " + weatherModel.city;
            forecasts.add(forecast);
        }
        return forecasts;

    }
}
