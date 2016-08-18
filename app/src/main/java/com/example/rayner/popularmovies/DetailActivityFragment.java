package com.example.rayner.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.rayner.popularmovies.model.MovieDBVideos;
import com.example.rayner.popularmovies.model.MovieVideo;
import com.example.rayner.popularmovies.model.db.MovieDBContract;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Vector;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 201;
    private static final int DETAIL_VIDEO_LOADER = 202;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();

    private Uri mUri;
    private Uri mVideoByIdUri;
    private String mMovieId;

    // Setup Butterknife views
    @BindView(R.id.detail_title) TextView detail_title;
    @BindView(R.id.detail_release_date) TextView detail_release_date;
    @BindView(R.id.detail_rating) TextView detail_rating;
    @BindView(R.id.detail_overview) TextView detail_overview;
    @BindView(R.id.detail_image) ImageView detail_image;

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
            MovieDBContract.MovieEntry.COLUMN_POPULARITY
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

    // Video DB Projection
    private static final String[] VIDEO_COLUMNS = {
            MovieDBContract.VideoEntry._ID,
            MovieDBContract.VideoEntry.COLUMN_MOVIE_ID,
            MovieDBContract.VideoEntry.COLUMN_KEY,
            MovieDBContract.VideoEntry.COLUMN_NAME,
            MovieDBContract.VideoEntry.COLUMN_SITE
    };

    // VIDEO_COLUMNS column indices
    static final int VID__ID            = 0;
    static final int VID_COL_MOVIE_ID   = 1;
    static final int VID_COL_KEY        = 2;
    static final int VID_COL_NAME       = 3;
    static final int VID_COL_SITE       = 4;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        // Get the bundle arguments to populate mUri
        Bundle args = getArguments();
        if(null != args) {
            mUri = args.getParcelable(DetailActivityFragment.DETAIL_URI);
            mMovieId = MovieDBContract.MovieEntry.getIdFromUri(mUri);
            mVideoByIdUri = MovieDBContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_VIDEO_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        if(null != mMovieId) {
            loadTrailers(this);
        }
        super.onResume();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case DETAIL_LOADER:
                if ( null != mUri ) {
                    // Now create and return a CursorLoader that will take care of
                    // creating a Cursor for the data being displayed.
                    return new CursorLoader(
                            getActivity(),
                            mUri,
                            MOVIE_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;

            case DETAIL_VIDEO_LOADER:
                if (null != mVideoByIdUri) {
                    return new CursorLoader(
                            getActivity(),
                            mVideoByIdUri,
                            VIDEO_COLUMNS,
                            null,
                            null,
                            null
                    );
                }
                break;
        }

        return null;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case DETAIL_LOADER:
                if (null != data && data.moveToFirst()) {
                    String original_title = data.getString(COL_ORIGINAL_TITLE);
                    String poster_path = data.getString(COL_POSTER_PATH);
                    String overview = data.getString(COL_OVERVIEW);
                    String vote_average = data.getString(COL_VOTE_AVERAGE) + "/10"; // show rating/10
                    String release_date = data.getString(COL_RELEASE_DATE).split("-")[0]; // Get release year

                    // Set fields in the Activity
                    detail_title.setText(original_title);
                    detail_release_date.setText(release_date);
                    detail_rating.setText(vote_average);
                    detail_overview.setText(overview);

                    String path = getString(R.string.tmd_poster_base_url) + "/" + getString(R.string.tmd_image_size) + poster_path;
                    Picasso.with(getContext()).load(path).into(detail_image);
                }
                break;

            case DETAIL_VIDEO_LOADER:
                Log.d(LOG_TAG, "DETAIL_VIDEO_LOADER load finished. Items = " + data.getCount());

                // Load a layout inflater
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // Set insert point
                ViewGroup trailerContainer = (ViewGroup) getActivity().findViewById(R.id.detail_trailer_container);

                trailerContainer.removeAllViews();

                try {
                    while(data.moveToNext()) {
                        // Load view
                        View trailerView = inflater.inflate(R.layout.list_item_trailer, null);

                        // Set trailer name
                        TextView textView = (TextView) trailerView.findViewById(R.id.list_item_trailer_name);
                        textView.setText(data.getString(VID_COL_NAME));

                        // Set trailer thumbnail
                        ImageView imageView = (ImageView) trailerView.findViewById(R.id.list_item_trailer_thumbnail);
                        final String youTubeKey = data.getString(VID_COL_KEY);
                        Uri path = new Uri.Builder()
                                .scheme("http")
                                .authority("img.youtube.com")
                                .appendPath("vi")
                                .appendPath(youTubeKey)
                                .appendPath("hqdefault.jpg")
                                .build();
                        Picasso.with(getContext()).load(path).into(imageView);

                        // Add onClickListener
                        trailerView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(
                                        Intent.ACTION_VIEW,
                                        new Uri.Builder()
                                                .scheme("http")
                                                .authority("www.youtube.com")
                                                .appendPath("watch")
                                                .appendQueryParameter("v", youTubeKey)
                                                .build()
                                ));
                            }
                        });

                        // insert the view
                        trailerContainer.addView(trailerView, new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT));

                        // Refresh
                        trailerContainer.invalidate();
                        Log.d(LOG_TAG, "trailer container invalidated.");
                    }
                } finally {
                    data.close();
                }
                break;

            default:
                Log.e(LOG_TAG, "invalid Loader ID " + loader.getId());
                break;
        }
    }

    private void loadTrailers(final DetailActivityFragment detailActivityFragment) {
        // Prepare the canons!
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.tmd_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBAPI movieDBAPI = retrofit.create(MovieDBAPI.class);

        movieDBAPI.loadVideos(mMovieId, getString(R.string.api_key))
                .enqueue(new Callback<MovieDBVideos<MovieVideo>>() {
                    @Override
                    public void onResponse(Call<MovieDBVideos<MovieVideo>> call, Response<MovieDBVideos<MovieVideo>> response) {
                        List<MovieVideo> movieVideoList = response.body().getVideos();

                        // Create ContentValues Vector
                        Vector<ContentValues> contentValuesVector = new Vector<>(movieVideoList.size());

                        for (MovieVideo video : movieVideoList) {
                            ContentValues cv = video.getContentValues();
                            cv.put(MovieDBContract.VideoEntry.COLUMN_MOVIE_ID, mMovieId);
                            contentValuesVector.add(cv);
                        }

                        int inserted = 0;
                        // add to database
                        if ( contentValuesVector.size() > 0 ) {
                            ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                            contentValuesVector.toArray(contentValuesArray);
                            inserted = getContext().getContentResolver().bulkInsert(MovieDBContract.VideoEntry.CONTENT_URI, contentValuesArray);

                            Log.d(LOG_TAG, inserted + " trailers added to DB");        // Reload the trailer loader
                            getLoaderManager().restartLoader(DETAIL_VIDEO_LOADER, null, detailActivityFragment);
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDBVideos<MovieVideo>> call, Throwable t) {
                        Log.e(LOG_TAG, call.request().url() + ": failed: " + t);
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
