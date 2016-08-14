package com.example.rayner.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private String mSort_order;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSort_order = PreferenceManager.getDefaultSharedPreferences(this).getString(this.getString(R.string.pref_sort_by_key), this.getString(R.string.pref_sort_by_default_value));
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
}
