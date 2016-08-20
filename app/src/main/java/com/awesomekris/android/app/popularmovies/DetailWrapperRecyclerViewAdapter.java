package com.awesomekris.android.app.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.awesomekris.android.app.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by kris on 16/8/17.
 */
public class DetailWrapperRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = DetailWrapperRecyclerViewAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;
    //private static final int MOVIE_DETAIL_LOADER = 2;

    private static final String uFavoriteByMovieIdSelection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    private Context mContext;
    private String mTrailerKey;
    private int isFavorite;
    private Movie mMovie;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;

//    public static interface DetailWrapperCallback{
//        void onItemSelected(Uri movieDetailUri);
//
//    }

    public DetailWrapperRecyclerViewAdapter(Context context, Movie movie,ArrayList<Trailer> trailers, ArrayList<Review> reviews) {
        mContext = context;
        mMovie = movie;
        mTrailers = trailers;
        mReviews = reviews;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

            if (viewGroup instanceof RecyclerView) {
                int layoutId = -1;
                switch (viewType) {
                    case VIEW_TYPE_MOVIE_DETAIL: {
                        layoutId = R.layout.list_item_movie_detail;
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                        //view.setFocusable(true);
                        return new DetailViewHolder(view);
                    }
                    case VIEW_TYPE_TRAILER: {
                        layoutId = R.layout.list_item_trailer;
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                        //view.setFocusable(true);
                        return new TrailerViewHolder(view);
                    }
                    case VIEW_TYPE_REVIEW: {
                        layoutId = R.layout.list_item_review;
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
                        //view.setFocusable(true);
                        return new ReviewViewHolder(view);
                    }
                }
                return null;
            } else {
                throw new RuntimeException("Not bound to RecyclerViewSelection");
            }


        }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        switch (getItemViewType(position)){
            case VIEW_TYPE_MOVIE_DETAIL:

                Movie movie = mMovie;
                //set title
                ((DetailViewHolder) holder).mTitle.setText(movie.getTitle());

                //set poster
                Picasso.with(mContext).load(movie.getPoster()).into(((DetailViewHolder) holder).mImageView);

                //set release date
                ((DetailViewHolder) holder).mReleaseDate.setText(movie.getRelease_date());

                //set vote average
                ((DetailViewHolder) holder).mVoteAverage.setText(movie.getVote_average());

                //set overview
                ((DetailViewHolder) holder).mOverView.setText(movie.getOverview());

                //set favorite button
                isFavorite = Integer.valueOf(movie.getFavorite());

                if (isFavorite == 1){
                    ((DetailViewHolder) holder).mFavorite.setText("CANCEL FROM FAVORITE");
                }else{
                    ((DetailViewHolder) holder).mFavorite.setText("ADD TO FAVORITE");
                }
                break;
            case VIEW_TYPE_TRAILER:
                Trailer trailer = mTrailers.get(position-1);
                //set trailer title
                ((TrailerViewHolder)holder).mTitleTextView.setText(trailer.getName());

                //get trailer key
                mTrailerKey = trailer.getKey();


                break;
            case VIEW_TYPE_REVIEW:
                Review review = mReviews.get(position - mTrailers.size() -1);
                //set review author
                ((ReviewViewHolder)holder).mAuthorTextView.setText(review.getName());

                //set review content
                ((ReviewViewHolder)holder).mContentTextView.setText(review.getComment());

                break;
        }


    }


    @Override
    public int getItemViewType(int position) {

        if (position == 0)
            return VIEW_TYPE_MOVIE_DETAIL;

        if (position > 0 && position < mTrailers.size() + 1)
            return VIEW_TYPE_TRAILER;

        return VIEW_TYPE_REVIEW;
    }


    @Override
    public int getItemCount() {

        return mTrailers.size() + mReviews.size() + 1;
    }

    public class DetailViewHolder extends RecyclerView.ViewHolder{
        public final ImageView mImageView;
        public final TextView mTitle;
        public final TextView mReleaseDate;
        public final TextView mVoteAverage;
        public final TextView mOverView;
        public final Button mFavorite;

        public DetailViewHolder(View view) {
            super(view);
            mImageView = (ImageView)view.findViewById(R.id.backdrop_imageView);
            mTitle = (TextView)view.findViewById(R.id.title_textView);
            mReleaseDate = (TextView)view.findViewById(R.id.release_date);
            mVoteAverage = (TextView)view.findViewById(R.id.vote_average);
            mOverView = (TextView)view.findViewById(R.id.overView_detail);
            mFavorite = (Button)view.findViewById(R.id.favorite);
            mFavorite.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int updated = 0;
                    ContentValues movieValues = new ContentValues();
                    String[] selectionArgs = new String[]{mMovie.getMovie_id()};

                    if(isFavorite == 0){
                        mFavorite.setText("CANCEL FROM FAVORITE");
                        isFavorite = 1;
                        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite);
                        updated = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues,uFavoriteByMovieIdSelection,selectionArgs);

                    }else {
                        mFavorite.setText("ADD TO FAVORITE");
                        isFavorite = 0;
                        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, isFavorite);
                        updated = mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, movieValues,uFavoriteByMovieIdSelection,selectionArgs);

                    }
                    Log.d(LOG_TAG, "UpdateFavorite Complete. " + updated + " Updated");
                }
            });
        }

    }



    public class TrailerViewHolder extends RecyclerView.ViewHolder{
        public final TextView mTitleTextView;
        public TrailerViewHolder(View view) {
            super(view);
            mTitleTextView = (TextView) view.findViewById(R.id.list_item_trailer_textView);
            mTitleTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri trailerUri = Uri.parse("http://www.youtube.com/watch?v=" + mTrailerKey);
                    Intent intent = new Intent(Intent.ACTION_VIEW, trailerUri);
                    mContext.startActivity(intent);
                }
            });
        }
    }



    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        public final TextView mAuthorTextView;
        public final TextView mContentTextView;

        public ReviewViewHolder(View view) {
            super(view);
            mAuthorTextView = (TextView) view.findViewById(R.id.list_item_review_author_textView);
            mContentTextView = (TextView) view.findViewById(R.id.list_item_review_textView);

        }
    }




}
