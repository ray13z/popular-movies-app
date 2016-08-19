package com.example.rayner.popularmovies.model;

import android.content.ContentValues;

import com.example.rayner.popularmovies.model.db.MovieDBContract;

/**
 * Created by rayner on 19/8/16.
 */
public class MovieReview {
    private String id;
    private String author;
    private String content;
    private String url;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public ContentValues getContentValues() {
        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MovieDBContract.ReviewEntry.COLUMNS_REVIEW_ID, id);
        reviewValues.put(MovieDBContract.ReviewEntry.COLUMNS_AUTHOR, author);
        reviewValues.put(MovieDBContract.ReviewEntry.COLUMNS_CONTENT, content);
        reviewValues.put(MovieDBContract.ReviewEntry.COLUMNS_URL, url);

        return  reviewValues;
    }
}
