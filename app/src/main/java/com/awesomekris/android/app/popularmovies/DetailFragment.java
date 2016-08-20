package com.awesomekris.android.app.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesomekris.android.app.popularmovies.data.MovieContract;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "detail_uri";
    public static final String DETAIL_MOVIE_ID = "detail_movie_id";
    private Parcelable mState;
    private static final String CURRENT_KEY = "current_position";
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLinearLayoutManager;
    public DetailWrapperRecyclerViewAdapter mDetailWrapperRecyclerViewAdapter;

//    private static final int DETAIL_LOADER = 2;
//    private static final int TRAILER_LOADER = 3;
//    private static final int REVIEW_LOADER = 4;
    private long mId;
    private Uri mUri;
    private Movie mMovie;
    private ArrayList<Trailer> mTrailers;
    private ArrayList<Review> mReviews;

    public DetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        //setHasOptionsMenu(true);
    }


    @Override
    public void onResume() {
        if (mState != null) {
            mLinearLayoutManager.onRestoreInstanceState(mState);
            Log.d(LOG_TAG, "use position: " + mState);
        }
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);

//        Handler myHandler = new Handler(Looper.getMainLooper());
//        Runnable myRunnable = new Runnable() {
//            @Override
//            public void run() {
//                getMovieDetial(mId);
//                getTrailers(mId);
//                getReviews(mId);
//            }
//        };
//        myHandler.post(myRunnable);


        super.onResume();
    }


    @Override
    public void onPause() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
            mId = arguments.getLong(DetailFragment.DETAIL_MOVIE_ID);
        }
        else if (getActivity().getIntent() == null || getActivity().getIntent().getData() == null) {
            return null;
        }
        else{
            mUri = getActivity().getIntent().getData();
            mId = getActivity().getIntent().getBundleExtra(DetailFragment.DETAIL_MOVIE_ID).getLong(DetailFragment.DETAIL_MOVIE_ID);
        }

        if(mId != 0){
            mMovie = getMovieDetial(mId);
            mTrailers = getTrailers(mId);
            mReviews = getReviews(mId);
        }

        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setAutoMeasureEnabled(true);

        //staggeredGridLayoutManager = new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.VERTICAL);
        mDetailWrapperRecyclerViewAdapter = new DetailWrapperRecyclerViewAdapter(getActivity(), mMovie, mTrailers, mReviews);

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDetailWrapperRecyclerViewAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_KEY)) {
            mState = savedInstanceState.getParcelable(CURRENT_KEY);

        }


        return rootView;
    }

    class MyRunnable implements Runnable {
        long m_id = mId;
        @Override
        public void run() {
            mMovie = getMovieDetial(m_id);
            mTrailers = getTrailers(m_id);
            mReviews = getReviews(m_id);
        }
    }

    public Movie getMovieDetial(long movieID){
        Cursor mCursor = getActivity().getContentResolver().query(
                MovieContract.MovieEntry.buildMovieSearchByMovieIdUri(movieID),
                null,
                null,
                null,
                null
        );
        Movie movie = new Movie();
        if(mCursor != null){
            mCursor.moveToFirst();

            int titleIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_TITLE);
            String title = mCursor.getString(titleIndex);

            int posterPathIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
            String posterPath = mCursor.getString(posterPathIndex);

            int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
            String releaseDate = mCursor.getString(releaseDateIndex);

            int voteAverageIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
            Long voteAverage = mCursor.getLong(voteAverageIndex);

            int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_OVERVIEW);
            String overview = mCursor.getString(overviewIndex);

            int favoriteIndex = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
            int favorite = mCursor.getInt(favoriteIndex);

            movie.setMovie_id(Long.toString(movieID));
            movie.setTitle(title);
            movie.setPoster(posterPath);
            movie.setRelease_date(releaseDate);
            movie.setVote_average(Long.toString(voteAverage));
            movie.setOverview(overview);
            movie.setFavorite(Integer.toString(favorite));
        }
        mCursor.close();
        return movie;
    }

    public ArrayList<Trailer> getTrailers(long movieId){
        Cursor tCursor = getContext().getContentResolver().query(
                MovieContract.TrailerEntry.buildTrailerSearchByfMovieIdUri(movieId),
                null,
                null,
                null,
                null);
        ArrayList<Trailer> trailers = new ArrayList<Trailer>() {};
        if(tCursor != null){
            while (tCursor.moveToNext()){
                    int nameIndex = tCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE);
                    String name = tCursor.getString(nameIndex);
                    int keyIndex = tCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                    String key = tCursor.getString(keyIndex);
                    Trailer trailer = new Trailer();
                    trailer.setName(name);
                    trailer.setKey(key);
                    trailers.add(trailer);
            }
        }else{
            Trailer trailer = new Trailer();
            trailer.setName("No Title");
            trailer.setKey("No Key");
            trailers.add(trailer);
        }
        tCursor.close();
        return trailers;

    }

    public ArrayList<Review> getReviews(long movieId){
        Cursor rCursor = getContext().getContentResolver().query(
                MovieContract.ReviewEntry.buildReviewSearchByMovieId(movieId),
                null,
                null,
                null,
                null);
        ArrayList<Review> reviews = new ArrayList<Review>() {};
        if(rCursor != null){
            while (rCursor.moveToNext()){
                    int authorIndex = rCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
                    String author = rCursor.getString(authorIndex);
                    int contentIndex = rCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);
                    String content = rCursor.getString(contentIndex);
                    Review review = new Review();
                    review.setName(author);
                    review.setComment(content);
                    reviews.add(review);
            }
        }else {
            Review review = new Review();
            review.setName("No Author");
            review.setComment("No Content");
            reviews.add(review);
        }
        rCursor.close();
        return reviews;}


    @Override
    public void onSaveInstanceState(Bundle outState) {

        mState = mLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable(CURRENT_KEY,mState);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_sort_key)) ) {

        }
    }
}
