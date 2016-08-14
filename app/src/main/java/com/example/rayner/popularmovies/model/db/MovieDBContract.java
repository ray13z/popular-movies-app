package com.example.rayner.popularmovies.model.db;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by rayner on 4/8/16.
 */
public class MovieDBContract {
    public static final String CONTENT_AUTHORITY = "com.example.rayner.popularmovies.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIES = "movies"; // Should point to table name
    public static final String PATH_POPULAR = "popular";
    public static final String PATH_TOP_RATED = "top_rated";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" +
                PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" +
                PATH_MOVIES;

        public static final String TABLE_NAME = "movies";
        public static final String COLUMN_MOVIE_ID = "id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_FAVORITE = "favorite"; // Stores 1: yes and 0: no
        public static final String COLUMN_POPULARITY = "popularity";

        // To use with 'insert' - return Uri with row id
        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static String getIdFromUri(Uri uri) {
            // CA/movies/{id} -> id (i.e. 2nd path segment)
            return uri.getPathSegments().get(1);
        }
    }

}
