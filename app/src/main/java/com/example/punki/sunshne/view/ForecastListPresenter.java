package com.example.punki.sunshne.view;

import android.widget.ArrayAdapter;

import com.example.punki.sunshne.model.WeatherModel;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;

public class ForecastListPresenter implements Presenter<WeatherModel> {
    private final ArrayAdapter<String> arrayAdapter;

    public ForecastListPresenter(ArrayAdapter<String> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    public void display(WeatherModel weatherModel) {
        arrayAdapter.clear();
        if (weatherModel != null) {
            arrayAdapter.addAll(format(weatherModel));
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
