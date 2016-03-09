package com.example.rayner.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ArrayList<String> moviePosterPathList;
    private ArrayList<MovieItem> mGridData;
    private GridView mGridView;
    private MovieAdapter movieAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovieGrid();
    }

    private void updateMovieGrid() {
        // @ToDo
        // Load the moviePosterPathList with poster paths
        // Call async task

        new FetchMovieDataTask().execute(); // start download
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);


        // Code here
        mGridData = new ArrayList<>();
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        movieAdapter = new MovieAdapter(getActivity(), mGridData);
        mGridView.setAdapter(movieAdapter);

        // @ToDo
        // Add GridView click event below


        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.mainactivityfragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setMoviePosterPathList(String movieListJSONString) {
        ArrayList<String> pathList = new ArrayList<>();
        try {
            JSONObject movieListJson = new JSONObject(movieListJSONString);
            JSONArray results = movieListJson.getJSONArray("results");

            MovieItem item;
            for (int i = 0; i < results.length(); i++) {
                JSONObject result = results.optJSONObject(i);
                String original_title = result.getString("original_title");
                String poster_path = getString(R.string.tmd_base_url) + "/" + getString(R.string.tmd_image_size) + result.getString("poster_path"); // URL (take care of path separators)
                String overview = result.getString("overview"); // synopsis
                String vote_average = result.getString("vote_average"); // userRating
                String release_date = result.getString("release_date");

                item = new MovieItem(original_title, poster_path, overview, vote_average, release_date);

                mGridData.add(item);
            }
        } catch (JSONException e) {
//            e.printStackTrace();
            Log.e(LOG_TAG, "JSONException while parsing API response - " + e);
        }


    }

    private class FetchMovieDataTask extends AsyncTask<String, Void, Integer> {

        @Override
        protected Integer doInBackground(String... params) {
            String movieListJSONString = "";

            /**
             * Make call to TMD API for discover data
             */

            // get sort_by preference
            String sort_by_pref = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));

            Log.d(LOG_TAG, "sort_by_pref = " + sort_by_pref);

            // Declaring HTTPURLConnection and BufferedReader outside try-catch so it can be closed in finally
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                Uri.Builder uri_builder = new Uri.Builder();
                uri_builder.scheme("http")
                        .authority(getString(R.string.uri_authority))
                        .appendPath("3")
                        .appendPath("movie")
                        .appendPath(sort_by_pref)
                        .appendQueryParameter("api_key", getString(R.string.api_key));

                // debug
                Log.d(LOG_TAG, "URL = " + uri_builder.build().toString());
                URL url = new URL(uri_builder.build().toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer stringBuffer = new StringBuffer();

                if(inputStream == null)
                    return 1;

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;

                while((line = reader.readLine()) != null) {
                    stringBuffer.append(line + "\n");
                }

                if(stringBuffer.length() == 0)
                    return 1;

                movieListJSONString = stringBuffer.toString();

            } catch (java.io.IOException e) {
//                e.printStackTrace();
                Log.e(LOG_TAG, "IOException - " + e);
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
                if(reader != null)
                    try {
                        reader.close();
                    } catch (IOException e) {
//                        e.printStackTrace();
                        Log.e(LOG_TAG, "IOException - " + e);
                    }
            }

            // Set mGridData by parsing JSON
            setMoviePosterPathList(movieListJSONString);
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result)
        {
            super.onPostExecute(result);
            // Download complete. Lets update UI

            if (result == 0) {
                movieAdapter.setGridData(mGridData);
            } else {
                Toast.makeText(getActivity(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
