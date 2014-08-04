package com.example.punki.sunshne;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;

public class DetailActivity extends Activity {

    public static final String DETAIL_FRAGMENT = "detailFragment";
    private final String LOG_TAG = DetailActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            startDetailFragment();
        }
    }

    private DetailFragment startDetailFragment() {
        Log.v(LOG_TAG, "startDetailFragment");
        DetailFragment detailFragment = new DetailFragment();
        getFragmentManager().beginTransaction()
                .add(R.id.container, detailFragment, DETAIL_FRAGMENT)
                .commit();
        return detailFragment;
    }

    @Override
    protected void onStart() {
        //chwilowo nie uzywamy moze sie zostawiam jako eksperyment
        Log.v(LOG_TAG, "onStart");
        super.onStart();
        DetailFragment detailFragment =
                (DetailFragment) getFragmentManager().findFragmentByTag(DETAIL_FRAGMENT);
        if (detailFragment == null) {
            startDetailFragment();
        }
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
            Intent settingsIntent = new Intent(this,SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class DetailFragment extends Fragment {


        public DetailFragment() {
            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            processIntent(rootView);
            return rootView;
        }

        private void processIntent(View rootView) {
            String text = getTextFromIntent();
            TextView textView = (TextView) rootView.findViewById(R.id.detail_text);
            textView.setText(text);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.detailfragment, menu);
            share(menu.findItem(R.id.action_share));
        }

        private void share(MenuItem item) {
            if (item != null) {
                ShareActionProvider shareActionProvider
                        = (ShareActionProvider) item.getActionProvider();
                shareActionProvider.setShareIntent(createShareIntent());
            }
        }

        private Intent createShareIntent() {
            String shareData = getTextFromIntent() + " #SunshineApp";
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareData);
            return shareIntent;
        }

        public String getTextFromIntent() {
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
                return intent.getStringExtra(Intent.EXTRA_TEXT);
            }
            return "";
        }

    }
}
