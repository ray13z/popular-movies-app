package com.example.rayner.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.rayner.popularmovies.model.db.MovieDBContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 201;
    private Uri mUri;

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
        }

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(null != data && data.moveToFirst()) {
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
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}
