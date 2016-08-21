package com.example.rayner.popularmovies.model;

import android.content.ContentValues;

import com.example.rayner.popularmovies.model.db.MovieDBContract;

/**
 * Created by Rayner on 3/8/2016.
 */
public class MovieItem {
    private String id; // ID from MovieDBAPI
    private String original_title;
    private String overview; // synopsis
    private String release_date;
    private String poster_path; // only path
    private double popularity;
    private double vote_average; // userRating


    public String getId() { return id; }

    public String getOriginal_title() {
        return original_title;
    }

    public String getOverview() {
        return overview;
    }

    public String getRelease_date() {
        return release_date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public double getPopularity() {
        return popularity;
    }

    public double getVote_average() {
        return vote_average;
    }

    public ContentValues getContentValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieDBContract.MovieEntry.COLUMN_MOVIE_ID, id);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_ORIGINAL_TITLE, original_title);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_OVERVIEW, overview);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_POSTER_PATH, poster_path);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_POPULARITY, popularity);
        movieValues.put(MovieDBContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);

        return movieValues;
    }
}
