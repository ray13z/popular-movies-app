package com.example.rayner.popularmovies.model;

import android.content.ContentValues;

import com.example.rayner.popularmovies.model.db.MovieDBContract;

/**
 * Created by rayner on 16/8/16.
 */
public class MovieVideo {
    private String key;
    private String name;
    private String site;

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getSite() {
        return site;
    }

    public ContentValues getContentValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieDBContract.VideoEntry.COLUMN_KEY, key);
        movieValues.put(MovieDBContract.VideoEntry.COLUMN_NAME, name);
        movieValues.put(MovieDBContract.VideoEntry.COLUMN_SITE, site);

        return movieValues;
    }
}
