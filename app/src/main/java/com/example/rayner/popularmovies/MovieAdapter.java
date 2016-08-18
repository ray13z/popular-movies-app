package com.example.rayner.popularmovies;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.example.rayner.popularmovies.model.MovieItem;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rayner on 3/9/2016.
 */
public class MovieAdapter extends CursorAdapter {
    private static final String LOG_TAG = MovieAdapter.class.getSimpleName();
    private ArrayList<MovieItem> mGridData;
    private Context context;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    public void setGridData(ArrayList<MovieItem> mGridData) {
        this.mGridData = mGridData;
        notifyDataSetChanged();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_view_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        // Load image using Picasso library
        String path = context.getString(R.string.tmd_poster_base_url) + "/" + context.getString(R.string.tmd_image_size) + cursor.getString(MainActivityFragment.COL_POSTER_PATH);

        Picasso.with(context).load(path).into(viewHolder.imageView);
    }


    public static class ViewHolder {
        public final ImageView imageView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.grid_item_image);
        }
    }
}
