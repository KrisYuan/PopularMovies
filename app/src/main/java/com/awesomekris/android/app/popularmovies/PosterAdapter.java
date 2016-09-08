package com.awesomekris.android.app.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.awesomekris.android.app.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by kris on 16/8/17.
 */
public class PosterAdapter extends CursorAdapter {

    private final String LOG_TAG = PosterAdapter.class.getSimpleName();
    private Context mContext;


    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView movieImageView;

        public ViewHolder(View view) {
            super(view);
            movieImageView = (ImageView) view.findViewById(R.id.grid_item_movie_imageView);
        }
    }

    public PosterAdapter(Context context, Cursor c, int flags){
        super(context, c, flags);
        Log.d(LOG_TAG, "PosterAdapter created");
        mContext = context;

    }



    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int layoutId = R.layout.grid_item_movie;


        View view = LayoutInflater.from(context).inflate(layoutId,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);


        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();


        int posterPathIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        final String posterPath = cursor.getString(posterPathIndex);


        //TODO callback
        Picasso.with(mContext).load(posterPath).into(viewHolder.movieImageView);

    }

}
