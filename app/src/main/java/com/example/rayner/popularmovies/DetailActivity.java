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

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = getIntent().getExtras();
        String original_title = bundle.getString("original_title");
        String poster_path = bundle.getString("poster_path");
        String overview = bundle.getString("overview");
        String vote_average = bundle.getString("vote_average") + "/10"; // show rating/10
        String release_date = bundle.getString("release_date").split("-")[0]; // Get release year

        // Set fields in the Activity
        ((TextView) findViewById(R.id.detail_title)).setText(original_title);
        ((TextView) findViewById(R.id.detail_release_date)).setText(release_date);
        ((TextView) findViewById(R.id.detail_rating)).setText(vote_average);
        ((TextView) findViewById(R.id.detail_overview)).setText(overview);

        Picasso.with(this).load(poster_path).into((ImageView) findViewById(R.id.detail_image));


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
