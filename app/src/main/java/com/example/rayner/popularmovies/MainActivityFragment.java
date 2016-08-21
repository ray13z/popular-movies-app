package com.example.rayner.popularmovies;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

import com.example.rayner.popularmovies.model.MovieDBMovies;
import com.example.rayner.popularmovies.model.MovieItem;
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
    private static final String SELECTED_KEY = "selected_position";
    private GridView mGridView;
    private MovieAdapter movieAdapter;
    private int mPosition = GridView.INVALID_POSITION;

    // Movie DB projection
    private static final String[] MOVIE_COLUMNS = {
            MovieDBContract.MovieEntry._ID,
            MovieDBContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieDBContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDBContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE,
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
    static final int COL_POPULARITY     = 7;

    // Favorites projection (INNER JOIN)
    private static final String[] FAVORITE_COLUMNS = {
            MovieDBContract.MovieEntry.TABLE_NAME + "." + MovieDBContract.MovieEntry._ID,
            MovieDBContract.MovieEntry.TABLE_NAME + "." + MovieDBContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDBContract.FavoriteEntry.COLUMN_POSTER
    };

    // FAVORITE_COLUMNS column indices
    static final int _FAV_ID                = 0;
    static final int COL_FAV_MOVIE_ID       = 1;
    static final int COL_FAV_POSTER         = 2;

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface MovieCallback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri movieUri);
    }


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

        if (sort_by_pref.equals("popular") || sort_by_pref.equals("top_rated")) {
            loadMoviesFromAPI(sort_by_pref);
        }
    }

    private void loadMoviesFromAPI(String sort_by_pref) {
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
                if(null != item) {
                    ((MovieCallback) getActivity())
                            .onItemSelected(MovieDBContract.MovieEntry.buildMovieUriWithMovieId(item.getString(COL_MOVIE_ID)));
                }
                mPosition = position;
            }
        });

        // Mine SavedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

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
    public void onSaveInstanceState(Bundle outState) {
        if(mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
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
        else if(sort_by_pref.equals("favorites")) {
            uri = MovieDBContract.FavoriteEntry.CONTENT_URI;

            // Return CursorLoader to handle MovieDBProvider's sFavoritesQueryBuilder INNER JOIN
            return new CursorLoader(getContext(),
                    uri,
                    FAVORITE_COLUMNS,
                    null,
                    null,
                    null
            );
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
        if(mPosition != GridView.INVALID_POSITION) {
            mGridView.smoothScrollToPosition(mPosition);
        }
        mGridView.setSelection(mPosition);
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
