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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.awesomekris.android.app.popularmovies.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String LOG_TAG = DetailFragment.class.getSimpleName();
    public static final String DETAIL_URI = "detail_uri";
    private static final int DETAIL_LOADER_ID = 2;

    private Uri mUri;
    private Parcelable mState;
    private static final String CURRENT_KEY = "current_position";
    public RecyclerView mRecyclerView;
    public LinearLayoutManager mLinearLayoutManager;
    public DetailWrapperRecyclerViewAdapter mDetailWrapperRecyclerViewAdapter;


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

        mUri = null;

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        else{
            if (getActivity().getIntent() != null && getActivity().getIntent().getData() != null) {
                mUri = getActivity().getIntent().getData();
            }
        }


        mLinearLayoutManager = new LinearLayoutManager(getContext());
        mLinearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mLinearLayoutManager.setAutoMeasureEnabled(true);


        mDetailWrapperRecyclerViewAdapter = new DetailWrapperRecyclerViewAdapter(getContext(), null, 1);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mDetailWrapperRecyclerViewAdapter);

        if (savedInstanceState != null && savedInstanceState.containsKey(CURRENT_KEY)) {
            mState = savedInstanceState.getParcelable(CURRENT_KEY);
        }

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(DETAIL_LOADER_ID,null,this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mUri != null)
            return new CursorLoader(getActivity(), mUri,null,null,null,null);
        else
            return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        int numOfTrailers = getNumOfTrailers(data);
        mDetailWrapperRecyclerViewAdapter.setNumOfTrailer(numOfTrailers);
        mDetailWrapperRecyclerViewAdapter.swapCursor(data);


    }

    private int getNumOfTrailers(Cursor data) {
        int numOfTrailer = 0;
        data.moveToFirst();
        while (data.moveToNext()) {
            if (data.getColumnIndex(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY) == -1)
                break;
            numOfTrailer += 1;
        }
        data.moveToFirst();
        return numOfTrailer;
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailWrapperRecyclerViewAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        mState = mLinearLayoutManager.onSaveInstanceState();
        outState.putParcelable(CURRENT_KEY,mState);

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ( key.equals(getString(R.string.pref_sort_key)) ) {
            getLoaderManager().restartLoader(DETAIL_LOADER_ID,null,this);
        }
    }



}
