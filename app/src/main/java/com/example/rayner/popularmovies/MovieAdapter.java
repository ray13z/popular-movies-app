package com.example.rayner.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.rayner.popularmovies.model.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 3/9/2016.
 */
public class MovieAdapter extends ArrayAdapter<MovieItem> {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<MovieItem> mGridData;
    private Context context;

    public MovieAdapter(Activity context, List<MovieItem> movieItemList) {
        // Will be setting an ImageView
        super(context, 0, movieItemList);
        this.context = context;
        this.mGridData = (ArrayList<MovieItem>) movieItemList;
    }

    public void setGridData(ArrayList<MovieItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the MovieItem object from the ArrayAdapter at the appropriate position
        MovieItem movieItem = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_view_item, parent, false);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.grid_item_image);

        // Load image using Picasso library
        String path = getContext().getString(R.string.tmd_poster_base_url) + "/" + getContext().getString(R.string.tmd_image_size) + movieItem.getPoster_path();
        Picasso.with(context).load(path).into(imageView);

        return convertView;
    }


}
