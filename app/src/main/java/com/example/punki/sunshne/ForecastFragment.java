package com.example.punki.sunshne;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.punki.sunshne.domain.Units;
import com.example.punki.sunshne.openweathermap.OpenWeatherFetchTask;
import com.example.punki.sunshne.view.ForecastListPresenter;
import com.example.punki.sunshne.mapper.UnitConverterMapper;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {

    private ArrayAdapter<String> weatherAdapter;

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        initForecastAdapter(rootView);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadDataFromServer();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setHasOptionsMenu(true);
    }

    private void initForecastAdapter(View rootView) {
        weatherAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(weatherAdapter);
        listView.setOnItemClickListener(buildItemClickListener());
    }

    private AdapterView.OnItemClickListener buildItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CharSequence text = parent.getItemAtPosition(position).toString();
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class);
                detailIntent.putExtra(Intent.EXTRA_TEXT, text);
                startActivity(detailIntent);
            }
        };
    }

    private void loadDataFromServer() {
        Param ffParam = ((ForecastFragmentParamSupplier) getActivity()).getForecastFragmentParam();

        UnitConverterMapper unitConverterMapper = new UnitConverterMapper(ffParam.units);
        FetchWeatherTask<OpenWeatherFetchTask.Param> fetchWeatherTask = new OpenWeatherFetchTask(
                new ForecastListPresenter(weatherAdapter,unitConverterMapper),
                getActivity().getContentResolver());

        fetchWeatherTask.execute(new OpenWeatherFetchTask.Param(ffParam.location, 7));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (R.id.action_refresh == item.getItemId()) {
            loadDataFromServer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class Param {
        public final String location;
        public final Units units;

        public Param(String location, Units units) {
            this.location = location;
            this.units = units;
        }
    }


    public static interface ForecastFragmentParamSupplier {
        Param getForecastFragmentParam();
    }
}
