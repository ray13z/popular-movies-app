package com.example.rayner.popularmovies;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rayner.popularmovies.model.MovieDBReviews;
import com.example.rayner.popularmovies.model.MovieDBVideos;
import com.example.rayner.popularmovies.model.MovieReview;
import com.example.rayner.popularmovies.model.MovieVideo;
import com.example.rayner.popularmovies.model.db.MovieDBContract;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private static final int DETAIL_REVIEW_LOADER = 203;
    private static final String LOG_TAG = DetailActivityFragment.class.getSimpleName();
    private static String sFavoriteByIdSelection = MovieDBContract.FavoriteEntry.TABLE_NAME + "." + MovieDBContract.FavoriteEntry.COLUMN_MOVIE_ID +
            " = ? ";

    private Uri mUri;
    private Uri mVideoByIdUri;
    private Uri mReviewByIdUri;
    private String mMovieId;
    private LayoutInflater mLayoutInflater;

    // Setup Butterknife views
    @BindView(R.id.detail_title) TextView detail_title;
    @BindView(R.id.detail_release_date) TextView detail_release_date;
    @BindView(R.id.detail_rating) TextView detail_rating;
    @BindView(R.id.detail_overview) TextView detail_overview;
    @BindView(R.id.detail_image) ImageView detail_image;
    @BindView(R.id.detail_trailer_heading) TextView detail_trailer_heading;
    @BindView(R.id.detail_trailer_divider) View detail_trailer_divider;
    @BindView(R.id.detail_review_heading) TextView detail_review_heading;
    @BindView(R.id.detail_review_divider) View detail_review_divider;
    @BindView(R.id.detail_instructions) TextView detail_instructions;
    @BindView(R.id.detail_favorite) CheckBox detail_favorite;

    // Movie DB projection
    private static final String[] MOVIE_COLUMNS = {
            MovieDBContract.MovieEntry._ID,
            MovieDBContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            MovieDBContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieDBContract.MovieEntry.COLUMN_OVERVIEW,
            MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE,
            MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE,
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

    // Review DB Projection
    private static final String[] REVIEW_COLUMNS = {
            MovieDBContract.ReviewEntry._ID,
            MovieDBContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieDBContract.ReviewEntry.COLUMNS_REVIEW_ID,
            MovieDBContract.ReviewEntry.COLUMNS_AUTHOR,
            MovieDBContract.ReviewEntry.COLUMNS_CONTENT,
            MovieDBContract.ReviewEntry.COLUMNS_URL
    };

    // REVIEW_COLUMNS column indices
    static final int REVIEW__ID            = 0;
    static final int REVIEW_COL_MOVIE_ID   = 1;
    static final int REVIEW_COL_REVIEW_ID  = 2;
    static final int REVIEW_COL_AUTHOR     = 3;
    static final int REVIEW_COL_CONTENT    = 4;
    static final int REVIEW_COL_URL        = 5;

    // Favorite DB Projection
    private static final String[] FAVORITE_COLUMNS = {
            MovieDBContract.FavoriteEntry._ID,
            MovieDBContract.FavoriteEntry.COLUMN_MOVIE_ID,
            MovieDBContract.FavoriteEntry.COLUMN_POSTER
    };

    // FAVORITE_COLUMNS column indices
    static final int FAVORITE_ID            = 0;
    static final int FAVORITE_COL_MOVIE_ID  = 1;
    static final int FAVORITE_COL_POSTER    = 2;

    public DetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        mLayoutInflater = inflater;

        ButterKnife.bind(this, view);

        // Get the bundle arguments to populate mUri
        Bundle args = getArguments();
        if(null != args) {
            mUri = args.getParcelable(DetailActivityFragment.DETAIL_URI);
            mMovieId = MovieDBContract.MovieEntry.getIdFromUri(mUri);
            mVideoByIdUri = MovieDBContract.VideoEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
            mReviewByIdUri = MovieDBContract.ReviewEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build();
        }

        // Setup Favorite onClick events
        detail_favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(((CheckBox) view).isChecked()) {
                    Uri inserted = getContext().getContentResolver().insert(MovieDBContract.FavoriteEntry.CONTENT_URI, getFavoritesContentValues());
                    Toast.makeText(getContext(), "Added to favorites!", Toast.LENGTH_SHORT).show();
                }
                else {
                    int deleted = getContext().getContentResolver().delete(MovieDBContract.FavoriteEntry.CONTENT_URI, sFavoriteByIdSelection , new String[] { mMovieId });
                    Toast.makeText(getContext(), "Removed from  favorites!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    private ContentValues getFavoritesContentValues() {
        ContentValues values = new ContentValues();
        values.put(MovieDBContract.FavoriteEntry.COLUMN_MOVIE_ID, mMovieId);
        values.put(MovieDBContract.FavoriteEntry.COLUMN_POSTER, getBitmapPosterData());
        return values;
    }

    private byte[] getBitmapPosterData() {
        Bitmap bitmap = ((BitmapDrawable) detail_image.getDrawable()).getBitmap();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        byte[] bitStream = outputStream.toByteArray();
        try {
            outputStream.close();
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(LOG_TAG, e.getLocalizedMessage());
        }
        return bitStream;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_VIDEO_LOADER, null, this);
        getLoaderManager().initLoader(DETAIL_REVIEW_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        if(null != mMovieId) {
            // Show instructions and hide trailer/review headers
            detail_instructions.setVisibility(View.INVISIBLE);
            detail_trailer_divider.setVisibility(View.VISIBLE);
            detail_trailer_heading.setVisibility(View.VISIBLE);
            detail_review_heading.setVisibility(View.VISIBLE);
            detail_review_divider.setVisibility(View.VISIBLE);
            detail_favorite.setVisibility(View.VISIBLE);

            // Set favorite button state
            detail_favorite.setChecked(isMovieFavorited());

            loadTrailers(this);
            loadReviews(this);
        } else {
            // Show instructions and hide trailer/review headers
            detail_instructions.setVisibility(View.VISIBLE);
            detail_trailer_divider.setVisibility(View.INVISIBLE);
            detail_trailer_heading.setVisibility(View.INVISIBLE);
            detail_review_heading.setVisibility(View.INVISIBLE);
            detail_review_divider.setVisibility(View.INVISIBLE);
            detail_favorite.setVisibility(View.INVISIBLE);
        }
        super.onResume();
    }

    private boolean isMovieFavorited() {
        if (getContext().getContentResolver().query(
                MovieDBContract.FavoriteEntry.CONTENT_URI.buildUpon().appendPath(mMovieId).build(),
                FAVORITE_COLUMNS,
                null,
                null,
                null
        ).getCount() > 0) {
            return true;
        }
        return false;
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

            case DETAIL_REVIEW_LOADER:
                if (null != mReviewByIdUri) {
                    return new CursorLoader(
                            getActivity(),
                            mReviewByIdUri,
                            REVIEW_COLUMNS,
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

                // Load a layout inflater
                if(null == mLayoutInflater) {
                    mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }

                // Set insert point
                ViewGroup trailerContainer = (ViewGroup) getActivity().findViewById(R.id.detail_trailer_container);

                trailerContainer.removeAllViews();

                try {
                    while(data.moveToNext()) {
                        // Load view
                        View trailerView = mLayoutInflater.inflate(R.layout.list_item_trailer, null);

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
                    }
                } finally {
                    data.close();
                }
                break;

            case DETAIL_REVIEW_LOADER:

                // Load a layout inflater
                if(null == mLayoutInflater) {
                    mLayoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }

                // Set insert point
                ViewGroup reviewContainer = (ViewGroup) getActivity().findViewById(R.id.detail_review_container);

                reviewContainer.removeAllViews();

                try {
                    while (data.moveToNext()) {
                        // Load view
                        View trailerView = mLayoutInflater.inflate(R.layout.list_item_review, null);

                        // Set review author name
                        TextView authorTextView = (TextView) trailerView.findViewById(R.id.list_item_review_author);
                        authorTextView.setText(data.getString(REVIEW_COL_AUTHOR));

                        // Set review content
                        final TextView contentTextView = (TextView) trailerView.findViewById(R.id.list_item_review_content);
                        contentTextView.setEllipsize(TextUtils.TruncateAt.END);
                        contentTextView.setText(data.getString(REVIEW_COL_CONTENT));
                        contentTextView.setMaxLines(4);

                        // Set collapsible textview
                        final View expandMoreView = trailerView.findViewById(R.id.list_item_expand_more);
                        final View expandLessView = trailerView.findViewById(R.id.list_item_expand_less);

                        // Tidy up - only show ic_expand_more if ellipsized
                        setupCollapsibleReviewTextView(contentTextView, expandMoreView);

                        contentTextView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                expandCollapseReviewTextView(view, expandMoreView, expandLessView);
                            }
                        });

                        // insert the view
                        reviewContainer.addView(trailerView, new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, AppBarLayout.LayoutParams.MATCH_PARENT));

                        // Refresh
                        reviewContainer.invalidate();
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

    private void setupCollapsibleReviewTextView(final TextView contentTextView, final View expandMoreView) {
        ViewTreeObserver viewTreeObserver = contentTextView.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int lineCount = contentTextView.getLineCount();

                if (contentTextView.getLayout().getEllipsisCount(lineCount-1) == 0) {
                    // Hide ic_expand_more
                    expandMoreView.setVisibility(View.INVISIBLE);
                    removeOnGlobalLayoutListener(this, contentTextView);    // Remove the Layout listener
                }
            }
        });
    }

    private void removeOnGlobalLayoutListener(ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener, TextView contentTextView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            contentTextView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        }
        else {
            contentTextView.getViewTreeObserver().removeGlobalOnLayoutListener(onGlobalLayoutListener);
        }
    }

    private void expandCollapseReviewTextView(View view, View expandMoreView, View expandLessView) {
        if(expandLessView.getVisibility() == View.INVISIBLE) {
            expandMoreView.setVisibility(View.INVISIBLE);
            expandLessView.setVisibility(View.VISIBLE);
            ((TextView) view).setMaxLines(Integer.MAX_VALUE);
        }
        else {
            expandMoreView.setVisibility(View.VISIBLE);
            expandLessView.setVisibility(View.INVISIBLE);
            ((TextView) view).setMaxLines(4);
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
                        if(null != response) {
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
                            // Changelog - added NPE check for getContentResolver()
                            if (null != getContext() && contentValuesVector.size() > 0) {
                                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                                contentValuesVector.toArray(contentValuesArray);
                                inserted = getContext().getContentResolver().bulkInsert(MovieDBContract.VideoEntry.CONTENT_URI, contentValuesArray);

                                getLoaderManager().restartLoader(DETAIL_VIDEO_LOADER, null, detailActivityFragment);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDBVideos<MovieVideo>> call, Throwable t) {
                        Log.e(LOG_TAG, call.request().url() + ": failed: " + t);
                    }
                });
    }

    private void loadReviews(final DetailActivityFragment detailActivityFragment) {
        // Prepare the canons!
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.tmd_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MovieDBAPI movieDBAPI = retrofit.create(MovieDBAPI.class);

        movieDBAPI.loadReviews(mMovieId, getString(R.string.api_key))
                .enqueue(new Callback<MovieDBReviews<MovieReview>>() {
                    @Override
                    public void onResponse(Call<MovieDBReviews<MovieReview>> call, Response<MovieDBReviews<MovieReview>> response) {
                        if(null != response) {
                            List<MovieReview> movieReviewList = response.body().getReviews();

                            // Create ContentValues Vector
                            Vector<ContentValues> contentValuesVector = new Vector<>(movieReviewList.size());

                            for (MovieReview video : movieReviewList) {
                                ContentValues cv = video.getContentValues();
                                cv.put(MovieDBContract.ReviewEntry.COLUMN_MOVIE_ID, mMovieId);
                                contentValuesVector.add(cv);
                            }

                            int inserted = 0;
                            // add to database
                            // Changelog - added NPE check for getContentResolver()
                            if (null != getContext() && contentValuesVector.size() > 0) {
                                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                                contentValuesVector.toArray(contentValuesArray);
                                inserted = getContext().getContentResolver().bulkInsert(MovieDBContract.ReviewEntry.CONTENT_URI, contentValuesArray);

                                getLoaderManager().restartLoader(DETAIL_REVIEW_LOADER, null, detailActivityFragment);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<MovieDBReviews<MovieReview>> call, Throwable t) {
                        Log.e(LOG_TAG, call.request().url() + ": failed: " + t);
                    }
                });

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
