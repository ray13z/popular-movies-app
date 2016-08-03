package com.example.rayner.popularmovies.model;

import java.util.List;

/**
 * Created by rayner on 2/8/16.
 * Used to store movies pulled from TheMovieDB.
 */
public class MovieDBMovies<MovieItem> {
    List<MovieItem> results;

    public List<MovieItem> getMovies() {
        return results;
    }
}
