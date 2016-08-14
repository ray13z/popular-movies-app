package com.example.rayner.popularmovies.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rayner.popularmovies.model.db.MovieDBContract.MovieEntry;

/**
 * Created by rayner on 4/8/16.
 */
public class MovieDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "movies.db";
    private static final int DATABASE_VERSION = 2;

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

    public MovieDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
