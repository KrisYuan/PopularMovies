package com.awesomekris.android.app.popularmovies;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.awesomekris.android.app.popularmovies.sync.PopularMoviesSyncAdapter;

public class MainMovieActivity extends AppCompatActivity implements MainMovieFragment.ShowMovieDetailCallBack{

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "MDFT";
    private String mSort;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_movie);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSort = Utility.getPreferredSort(this);

        if(findViewById(R.id.detail_movie_container) != null){
            mTwoPane = true;
            if(savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.detail_movie_container, new DetailFragment(), MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();

                //TODO only call updateFromInternet when click refresh
                //TODO sync when first launched
                //updateMovieDataFromInternet();
            }

        } else{
            mTwoPane = false;
        }

        PopularMoviesSyncAdapter.initializeSyncAdapter(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        String sort = Utility.getPreferredSort(this);
        if(sort != null && !sort.equals(mSort)){
            onSortChanged();
        }
        mSort = sort;
    }


    private void onSortChanged(){
        MainMovieFragment mmaf = (MainMovieFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movie_poster);
        if(null != mmaf){
            mmaf.onSortChanged();
        }
    }


    @Override
    public void onItemSelected(Uri movieDetailUri) {

        if(findViewById(R.id.detail_movie_container) != null){
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,movieDetailUri);
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, fragment, MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }else{
            Intent intent = new Intent(this,DetailActivity.class);
            intent.setData(movieDetailUri);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_movie, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }

}
