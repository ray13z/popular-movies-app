package com.example.rayner.popularmovies;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {
    // Setup Butterknife views
    @BindView(R.id.detail_title) TextView detail_title;
    @BindView(R.id.detail_release_date) TextView detail_release_date;
    @BindView(R.id.detail_rating) TextView detail_rating;
    @BindView(R.id.detail_overview) TextView detail_overview;
    @BindView(R.id.detail_image) ImageView detail_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        String original_title = bundle.getString("original_title");
        String poster_path = bundle.getString("poster_path");
        String overview = bundle.getString("overview");
        String vote_average = bundle.getString("vote_average") + "/10"; // show rating/10
        String release_date = bundle.getString("release_date").split("-")[0]; // Get release year

        // Set fields in the Activity
        detail_title.setText(original_title);
        detail_release_date.setText(release_date);
        detail_rating.setText(vote_average);
        detail_overview.setText(overview);

        String path = getString(R.string.tmd_poster_base_url) + "/" + getString(R.string.tmd_image_size) + poster_path;
        Picasso.with(this).load(path).into(detail_image);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
