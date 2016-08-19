package com.example.rayner.popularmovies.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rayner.popularmovies.model.db.MovieDBContract.MovieEntry;
import com.example.rayner.popularmovies.model.db.MovieDBContract.VideoEntry;
import com.example.rayner.popularmovies.model.db.MovieDBContract.ReviewEntry;

/**
 * Created by rayner on 4/8/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
            MovieEntry._ID + " INTEGER PRIMARY KEY, " +
            MovieEntry.COLUMN_MOVIE_ID + " INTEGER UNIQUE NOT NULL, " +
            MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
            MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
            MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL, " +
            MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL " +
            ");";

    private static final String SQL_CREATE_VIDEOS_TABLE = "CREATE TABLE " + VideoEntry.TABLE_NAME + " (" +
            VideoEntry._ID + " INTEGER PRIMARY KEY, " +
            VideoEntry.COLUMN_KEY + " TEXT UNIQUE NOT NULL, " +
            VideoEntry.COLUMN_NAME + " TEXT NOT NULL, " +
            VideoEntry.COLUMN_SITE + " TEXT NOT NULL, " +
            VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + VideoEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
            MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ") " +
            ");";

    private static final String SQL_CREATE_REVIEWS_TABLE = "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
            ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
            ReviewEntry.COLUMNS_REVIEW_ID + " TEXT UNIQUE NOT NULL, " +
            ReviewEntry.COLUMNS_AUTHOR + " TEXT NOT NULL, " +
            ReviewEntry.COLUMNS_CONTENT + " TEXT NOT NULL, " +
            ReviewEntry.COLUMNS_URL + " TEXT NOT NULL, " +
            ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            // Set up the location column as a foreign key to location table.
            " FOREIGN KEY (" + ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
            MovieEntry.TABLE_NAME + " (" + MovieEntry.COLUMN_MOVIE_ID + ") " +
            ");";

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEOS_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEWS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
