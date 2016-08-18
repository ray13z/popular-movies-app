package com.example.rayner.popularmovies.model.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by rayner on 4/8/16.
 */
public class MovieDBProvider  extends ContentProvider{

    private static final int CODE_MOVIE = 100;
    private static final int CODE_MOVIE_WITH_ID = 101;
    private static final int CODE_MOVIE_POPULAR = 102;
    private static final int CODE_MOVIE_TOP_RATED = 103;
    private static final int CODE_VIDEO = 104;
    private static final int CODE_VIDEO_WITH_ID = 105;

    private static UriMatcher sUriMatcher = buildUriMatcher();
    private static String sMovieByIdSelection = MovieDBContract.MovieEntry.COLUMN_MOVIE_ID +
            " = ? ";
    private static String sSortMoviesByPopular = MovieDBContract.MovieEntry.COLUMN_POPULARITY +
            " DESC ";
    private static String sSortMoviesByTopRated = MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE +
            " DESC ";;
    private String sVideoByIdSelection = MovieDBContract.VideoEntry.COLUMN_MOVIE_ID +
            " = ? ";

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieDBContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieDBContract.PATH_MOVIES, CODE_MOVIE);
        matcher.addURI(authority, MovieDBContract.PATH_MOVIES + "/#", CODE_MOVIE_WITH_ID);
        matcher.addURI(authority, MovieDBContract.PATH_MOVIES + "/" + MovieDBContract.PATH_POPULAR, CODE_MOVIE_POPULAR);
        matcher.addURI(authority, MovieDBContract.PATH_MOVIES + "/" + MovieDBContract.PATH_TOP_RATED, CODE_MOVIE_TOP_RATED);
        matcher.addURI(authority, MovieDBContract.PATH_VIDEOS, CODE_VIDEO);
        matcher.addURI(authority, MovieDBContract.PATH_VIDEOS + "/#", CODE_VIDEO_WITH_ID);

        return matcher;
    }

    private MovieDBHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        // Init DB Helper
        mOpenHelper = new MovieDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movies"
            case CODE_MOVIE:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieDBContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // "movies/{id}"
            case CODE_MOVIE_WITH_ID:
                retCursor = getMovieById(uri, projection, sortOrder);
                break;

            // "movies/popular"
            case CODE_MOVIE_POPULAR:
                retCursor = getPopularMovies(uri, projection);
                break;

            // "movies/top_rated"
            case CODE_MOVIE_TOP_RATED:
                retCursor = getTopRatedMovies(uri, projection);
                break;

            // "videos"
            case CODE_VIDEO:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieDBContract.VideoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            // "videos/{id}"
            case CODE_VIDEO_WITH_ID:
                retCursor = getVideoById(uri, projection, sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Register observer
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    private Cursor getVideoById(Uri uri, String[] projection, String sortOrder) {
        String id = MovieDBContract.VideoEntry.getIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieDBContract.VideoEntry.TABLE_NAME,
                projection,
                sVideoByIdSelection,
                new String[] { id },
                null,
                null,
                sortOrder
        );
    }

    private Cursor getTopRatedMovies(Uri uri, String[] projection) {
        return mOpenHelper.getReadableDatabase().query(
                MovieDBContract.MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sSortMoviesByTopRated
        );
    }

    private Cursor getPopularMovies(Uri uri, String[] projection) {
        return mOpenHelper.getReadableDatabase().query(
                MovieDBContract.MovieEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sSortMoviesByPopular
        );
    }

    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {
        String id = MovieDBContract.MovieEntry.getIdFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieDBContract.MovieEntry.TABLE_NAME,
                projection,
                sMovieByIdSelection,
                new String[] { id },
                null,
                null,
                sortOrder
        );
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            // "movies"
            case CODE_MOVIE:
                return MovieDBContract.MovieEntry.CONTENT_TYPE;

            // "movies/{id}"
            case CODE_MOVIE_WITH_ID:
                return MovieDBContract.MovieEntry.CONTENT_ITEM_TYPE;

            // "movies/popular"
            case CODE_MOVIE_POPULAR:
                return MovieDBContract.MovieEntry.CONTENT_TYPE;

            // "movies/top_rated"
            case CODE_MOVIE_TOP_RATED:
                return MovieDBContract.MovieEntry.CONTENT_TYPE;

            // "videos"
            case CODE_VIDEO:
                return MovieDBContract.VideoEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            // "movies"
            case CODE_MOVIE:
                _id = db.insertWithOnConflict(MovieDBContract.MovieEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if(_id > 0) {
                    returnUri = MovieDBContract.MovieEntry.buildMovieUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;

            // "videos"
            case CODE_VIDEO:
                _id = db.insertWithOnConflict(MovieDBContract.VideoEntry.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                if(_id > 0) {
                    returnUri = MovieDBContract.VideoEntry.buildMovieUri(_id);
                }
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;


            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        // Notify
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;

        switch (match) {
            // "movies"
            case CODE_MOVIE:
                db.beginTransaction();
                try {
                    for (ContentValues value :
                            values) {
                        long _id = db.insertWithOnConflict(MovieDBContract.MovieEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if(_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;

            // "videos"
            case CODE_VIDEO:
                db.beginTransaction();
                try {
                    for (ContentValues value :
                            values) {
                        long _id = db.insertWithOnConflict(MovieDBContract.VideoEntry.TABLE_NAME, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                        if(_id != -1)
                            returnCount++;
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;

            default:
                return super.bulkInsert(uri, values);
        }


        getContext().getContentResolver().notifyChange(uri, null);
        return returnCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case CODE_MOVIE:
                rowsDeleted = db.delete(
                        MovieDBContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CODE_VIDEO:
                rowsDeleted = db.delete(
                        MovieDBContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CODE_MOVIE:
                rowsUpdated = db.update(MovieDBContract.MovieEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case CODE_VIDEO:
                rowsUpdated = db.update(MovieDBContract.VideoEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
