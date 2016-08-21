package com.example.rayner.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements MainActivityFragment.MovieCallback {

    private static final String DETAILACTIVITYFRAGMENT_TAG = "DFTAG";
    private String mSort_order;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSort_order = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.pref_sort_by_key), this.getString(R.string.pref_sort_by_default_value));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(findViewById(R.id.movie_detail_container) != null) {
            // Movie container view present in layout-sw600dp
            mTwoPane = true;

            // If being restored from previous state, avoid overlapping fragments
            if(savedInstanceState != null) {
                return;
            }

            // Show detail view in this activity by adding/replacing detail fragment using fragment transaction
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILACTIVITYFRAGMENT_TAG)
                    .commit();
        }
        else {
            mTwoPane = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        String sort_order = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.pref_sort_by_key), this.getString(R.string.pref_sort_by_default_value));

        if(sort_order != null && !sort_order.equals(mSort_order)) {
            MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main);
            fragment.onPreferenceChanged();
            mSort_order = sort_order;
        }
        super.onResume();
    }

    @Override
    public void onItemSelected(Uri movieUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, movieUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILACTIVITYFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(movieUri);
            startActivity(intent);
        }
    }
}
