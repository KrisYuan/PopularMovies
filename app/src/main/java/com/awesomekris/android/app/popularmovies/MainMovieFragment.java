package com.awesomekris.android.app.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.awesomekris.android.app.popularmovies.data.MovieContract;
import com.awesomekris.android.app.popularmovies.sync.PopularMoviesSyncAdapter;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainMovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,SharedPreferences.OnSharedPreferenceChangeListener{

    public static final String LOG_TAG = MainMovieFragment.class.getSimpleName();
    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_LOADER_ID=1;

    private GridView mGridView;
    private PosterAdapter mPosterAdapter;
    private String mSort;
    private Parcelable mState;

    private static final String SORT_POPULARITY = "popular";
    private static final String SORT_TOP_RATED = "top_rated";
    private static final String SORT_FAVORITE = "favorite";


    public MainMovieFragment() {
    }


    public interface ShowMovieDetailCallBack {
        // when poster was clicked, call this method
        void onItemSelected(Uri movieDetailUri);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onStart() {
        super.onStart();
        onSortChanged();
    }
    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
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

        View rootView =  inflater.inflate(R.layout.fragment_main_movie, container, false);
        PopularMoviesSyncAdapter.syncImmediately(getActivity());

        //mSort = Utility.getPreferredSort(getContext());
        //onSortChanged();

        mPosterAdapter = new PosterAdapter(getActivity(),null,0);
        mGridView = (GridView)rootView.findViewById(R.id.gridView_movie);
        mGridView.setAdapter(mPosterAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor movieCursor = (Cursor) parent.getAdapter().getItem(position);
                if (movieCursor != null) {

                    int movieIdIndex = movieCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                    long movieId = movieCursor.getLong(movieIdIndex);

                    Uri movieUri = MovieContract.MovieEntry.buildDetailMovieItemUri(movieId);
                    ((ShowMovieDetailCallBack)getActivity()).onItemSelected(movieUri);
                }
            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mState = savedInstanceState.getParcelable(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(MOVIE_LOADER_ID,null,this);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mSort = Utility.getPreferredSort(getActivity());
        String sortOrder;
        Uri uri;
        switch (mSort){
            case SORT_POPULARITY:
                sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC" + " LIMIT 20";
                uri = MovieContract.MovieEntry.buildPopularMoviesUri();
                break;
            case SORT_TOP_RATED:
                sortOrder = MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC" + " LIMIT 20";
                uri = MovieContract.MovieEntry.buildTopRatedMoviesUri();
                Log.v(LOG_TAG, "use top rate uri");
                break;
            case SORT_FAVORITE:
                sortOrder = MovieContract.MovieEntry.COLUMN_FAVORITE + " DESC";
                uri = MovieContract.MovieEntry.buildFavoriteMoviesUri();
                break;
            default:
                //TODO raise exception
                return null;
        }
        return new CursorLoader(getActivity(), uri, null,null,null,sortOrder);

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "uri size is " + data.getCount());
        mPosterAdapter.swapCursor(data);
        if (mState != null) {
            mGridView.onRestoreInstanceState(mState);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPosterAdapter.swapCursor(null);
    }


    public void onSortChanged(){
        getLoaderManager().restartLoader(MOVIE_LOADER_ID,null,this);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        mState = mGridView.onSaveInstanceState();
        outState.putParcelable(SELECTED_KEY,mState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.v(LOG_TAG, "preference change, invoke restartLoader");

        if ( key.equals(getString(R.string.pref_sort_key)) ) {
            onSortChanged();
            Log.v(LOG_TAG, "uri change, invoke restartLoader");
        }
    }

}
