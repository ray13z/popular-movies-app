package com.example.rayner.popularmovies;

import android.content.Intent;
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
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.rayner.popularmovies.model.MovieItem;
import com.example.rayner.popularmovies.model.MovieDBMovies;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    public void onResume() {
        super.onResume();
        movieAdapter.clear();
        updateMovieGrid();
    }

    private void updateMovieGrid() {

        String sort_by_pref = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));


        // Retrofit calls
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.tmd_base_url))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MovieDBAPI movieDBAPI = retrofit.create(MovieDBAPI.class);

        movieDBAPI.loadMovies(sort_by_pref, getString(R.string.api_key))
                .enqueue(new Callback<MovieDBMovies<MovieItem>>() {
                    @Override
                    public void onResponse(Call<MovieDBMovies<MovieItem>> call, Response<MovieDBMovies<MovieItem>> response) {
                        Log.i(LOG_TAG, response.body().toString());
                        for (MovieItem movie : response.body().getMovies()) {
                            mGridData.add(movie);
                        }

                        movieAdapter.setGridData(mGridData);
                    }

                    @Override
                    public void onFailure(Call<MovieDBMovies<MovieItem>> call, Throwable t) {
                        Log.e(LOG_TAG, call.request().url() + ": failed: " + t);
                    }
                });
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
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                MovieItem item = (MovieItem) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);


                //Pass MovieItem details
                intent.putExtra("original_title", item.getOriginal_title()).
                        putExtra("poster_path", item.getPoster_path()).
                        putExtra("overview", item.getOverview()).
                        putExtra("vote_average", item.getVote_average()).
                        putExtra("release_date", item.getRelease_date());

                //Start details activity
                startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
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
}
