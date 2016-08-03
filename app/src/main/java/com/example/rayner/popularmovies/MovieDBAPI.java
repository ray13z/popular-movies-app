package com.example.rayner.popularmovies;

import com.example.rayner.popularmovies.model.MovieItem;
import com.example.rayner.popularmovies.model.MovieDBMovies;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by rayner on 2/8/16.
 * REST API for Retrofit defined via the following interface.
 */
public interface MovieDBAPI {
    @GET("/3/movie/{sort_by_pref}")
    Call<MovieDBMovies<MovieItem>> loadMovies(@Path("sort_by_pref") String sort_by_pref, @Query("api_key") String api_key);
}
