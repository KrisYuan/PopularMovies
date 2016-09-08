package com.awesomekris.android.app.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import com.awesomekris.android.app.popularmovies.library.RecyclerViewCursorAdapter;
import com.squareup.picasso.Picasso;

/**
 * Created by kris on 16/8/17.
 */
public class DetailWrapperRecyclerViewAdapter extends RecyclerViewCursorAdapter<RecyclerView.ViewHolder> {

    private static final String LOG_TAG = DetailWrapperRecyclerViewAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_MOVIE_DETAIL = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;


    private static final String uFavoriteByMovieIdSelection = MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";


    private String mMovieId;
    private String mTrailerKey;
    private int isFavorite;
    private int mNumOfTrailer;

    public DetailWrapperRecyclerViewAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, Cursor cursor) {

        if(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE) != -1) {

            //get movie_id
            int movieIdIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            long movieId = cursor.getLong(movieIdIndex);
            mMovieId = Long.toString(movieId);

            //set title
            int titleIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            String title = cursor.getString(titleIndex);
            ((DetailViewHolder) holder).mTitle.setText(title);

            //set poster
            int posterIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            String poster = cursor.getString(posterIndex);
            Picasso.with(mContext).load(poster).into(((DetailViewHolder) holder).mImageView);

            //set release date
            int releaseDateIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            String releaseDate = cursor.getString(releaseDateIndex);
            ((DetailViewHolder) holder).mReleaseDate.setText(releaseDate);

            //set vote average
            int voteAverageIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            String voteAverage = cursor.getString(voteAverageIndex);
            ((DetailViewHolder) holder).mVoteAverage.setText(voteAverage);

            //set overview
            int overviewIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            String overview = cursor.getString(overviewIndex);
            ((DetailViewHolder) holder).mOverView.setText(overview);

            //set favorite button
            int favoriteIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
            int favorite = cursor.getInt(favoriteIndex);
            isFavorite = Integer.valueOf(favorite);

            if (isFavorite == 1) {
                ((DetailViewHolder) holder).mFavorite.setText("CANCEL FROM FAVORITE");
            } else {
                ((DetailViewHolder) holder).mFavorite.setText("ADD TO FAVORITE");
            }
        }

            if (cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY) != -1) {


                //set trailer title
                int titleIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE);
                String title = cursor.getString(titleIndex);
                ((TrailerViewHolder)holder).mTitleTextView.setText(title);

                //get trailer key
                int keyIndex = cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                mTrailerKey = cursor.getString(keyIndex);

            }


            if(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR) != -1) {

                //set review author
                int authorIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
                String author = cursor.getString(authorIndex);
                ((ReviewViewHolder)holder).mAuthorTextView.setText(author);

                //set review content
                int contentIndex = cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);
                String content = cursor.getString(contentIndex);
                ((ReviewViewHolder)holder).mContentTextView.setText(content);
            }

        }

    @Override
    protected void onContentChanged() {

    }

    public void setNumOfTrailer(int numOfTrailer) {
        mNumOfTrailer = numOfTrailer;
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
    public int getItemViewType(int position) {

        if (position == 0 )
            return VIEW_TYPE_MOVIE_DETAIL;

        if (position < mNumOfTrailer + 1)
            return VIEW_TYPE_TRAILER;

        return VIEW_TYPE_REVIEW;
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
                    String[] selectionArgs = new String[]{mMovieId};

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
