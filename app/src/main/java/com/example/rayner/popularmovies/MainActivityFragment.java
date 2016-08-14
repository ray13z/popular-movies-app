package com.example.rayner.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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
import com.example.rayner.popularmovies.model.db.MovieDBContract;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.Vector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private static final int MOVIE_LOADER = 101;
    private GridView mGridView;
    private MovieAdapter movieAdapter;

    // Movie DB projection
    private static final String[] MOVIE_COLUMNS = {
            MovieDBContract.MovieEntry._ID,
            MovieDBContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieDBContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDBContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieDBContract.MovieEntry.COLUMN_FAVORITE,
            MovieDBContract.MovieEntry.COLUMN_POPULARITY,
    };

    // MOVIE_COLUMNS column indices
    static final int _ID                = 0;
    static final int COL_MOVIE_ID       = 1;
    static final int COL_ORIGINAL_TITLE = 2;
    static final int COL_POSTER_PATH    = 3;
    static final int COL_OVERVIEW       = 4;
    static final int COL_VOTE_AVERAGE   = 5;
    static final int COL_RELEASE_DATE   = 6;
    static final int COL_FAVORITE       = 7;
    static final int COL_POPULARITY     = 8;



    public MainActivityFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @SuppressLint("NewApi")
    @Override
    public void onResume() {
        super.onResume();
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

                        // Get list of movies
                        List<MovieItem> movieItemList = response.body().getMovies();

                        // Create ContentValues Vector
                        Vector<ContentValues> contentValuesVector = new Vector<>(movieItemList.size());

                        for (MovieItem movie : movieItemList) {
                            contentValuesVector.add(movie.getContentValues());
                        }

                        int inserted = 0;
                        // add to database
                        if ( contentValuesVector.size() > 0 ) {
                            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                            contentValuesVector.toArray(contentValuesArray);
                            inserted = getContext().getContentResolver().bulkInsert(MovieDBContract.MovieEntry.CONTENT_URI, contentValuesArray);
                        }
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


        // Set Adapter for GridView
        mGridView = (GridView) rootView.findViewById(R.id.gridview);
        movieAdapter = new MovieAdapter(getActivity(), null, 0);
        mGridView.setAdapter(movieAdapter);

        // @ToDo
        // Add GridView click event below
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //Get item at position
                Cursor item = (Cursor) parent.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), DetailActivity.class);

                if(item!=null) {

                    //Pass MovieItem details
                    intent.putExtra("original_title", item.getString(COL_ORIGINAL_TITLE)).
                            putExtra("poster_path", item.getString(COL_POSTER_PATH)).
                            putExtra("overview", item.getString(COL_OVERVIEW)).
                            putExtra("vote_average", item.getString(COL_VOTE_AVERAGE)).
                            putExtra("release_date", item.getString(COL_RELEASE_DATE));

                    //Start details activity
                    startActivity(intent);
                }
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = MovieDBContract.MovieEntry.CONTENT_URI;

        String sort_by_pref = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_default_value));

        if(sort_by_pref.equals("popular")) {
            uri = uri.buildUpon().appendPath(MovieDBContract.PATH_POPULAR).build();
        }
        else if(sort_by_pref.equals("top_rated")) {
            uri = uri.buildUpon().appendPath(MovieDBContract.PATH_TOP_RATED).build();
        }
        else {
            Log.e(LOG_TAG, "Error: Illegal pref_sort_by_key " + sort_by_pref);
        }

        return new CursorLoader(getContext(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                null
                );
    }

    @SuppressLint("NewApi")
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        movieAdapter.swapCursor(data);
    }

    @SuppressLint("NewApi")
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        movieAdapter.swapCursor(null);
    }

    public void onPreferenceChanged() {
        updateMovieGrid();
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
}
