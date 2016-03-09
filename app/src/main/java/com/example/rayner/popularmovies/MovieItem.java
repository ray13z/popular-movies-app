package com.example.rayner.popularmovies;

/**
 * Created by Rayner on 3/8/2016.
 */
public class MovieItem {
    private String original_title;
    private String poster_path; // URL
    private String overview; // synopsis
    private String vote_average; // userRating
    private String release_date;

    public MovieItem(String original_title, String poster_path, String overview, String vote_average, String release_date) {
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.vote_average = vote_average;
        this.release_date = release_date;
    }

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
