package com.example.rayner.popularmovies.model;

/**
 * Created by Rayner on 3/8/2016.
 */
public class MovieItem {
    private String original_title;
    private String poster_path; // only path
    private String overview; // synopsis
    private String vote_average; // userRating
    private String release_date;

    public String getOriginal_title() {
        return original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }
}
