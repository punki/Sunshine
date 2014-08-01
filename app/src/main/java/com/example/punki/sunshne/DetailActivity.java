package com.example.punki.sunshne;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DetailActivity extends Activity {

    public static final String DETAIL_FRAGMENT = "detailFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            startDetailFragment();
        }
    }

    private DetailFragment startDetailFragment() {
        DetailFragment detailFragment = new DetailFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.container, detailFragment, DETAIL_FRAGMENT)
                .commit();
        return detailFragment;
    }

    @Override
    protected void onStart() {
        super.onStart();
        DetailFragment detailFragment =
                (DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
        if (detailFragment == null) {
            detailFragment = startDetailFragment();
        }
        detailFragment.updText(getIntent().getStringExtra("text"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class DetailFragment extends Fragment {

        private TextView textView;

        public void updText(String text) {
            textView.setText(text);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            textView = (TextView) rootView.findViewById(R.id.detail_forecast);
            return rootView;
        }
    }
}
