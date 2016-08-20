package com.awesomekris.android.app.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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
            }else{
                mTwoPane = false;
                getSupportActionBar().setElevation(0f);
            }

            if(savedInstanceState == null){
                //TODO only call updateFromInternet when click refresh
                //TODO sync when first launched
                updateMovieDataFromInternet();
            }

        }

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String sort = Utility.getPreferredSort(this);
        if(sort != null && !sort.equals(mSort)){

            updateMovieDataFromInternet();

        }
//        DetailFragment df = (DetailFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
//        if (null != df){
//            //TODO Update Movie Data According to MainMovieFragement
//            df.onSortChanged();
//
//        }
        mSort = sort;
    }


    private void updateMovieDataFromInternet(){
        MainMovieFragment mmaf = (MainMovieFragment)getSupportFragmentManager()
                .findFragmentById(R.id.fragment_movie_poster);
        if(null != mmaf){
            mmaf.getMoviesFromInternet();
        }

        if(mTwoPane){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container, new MainMovieFragment(),MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }

    }


    @Override
    public void onItemSelected(Uri movieDetailUri, long movie_id) {
        if (mTwoPane){

            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI,movieDetailUri);
            args.putLong(DetailFragment.DETAIL_MOVIE_ID,movie_id);
            DetailFragment fragment = new DetailFragment();
            //DetailFragment fragment = (DetailFragment)getSupportFragmentManager().findFragmentByTag(MOVIE_DETAIL_FRAGMENT_TAG);
            fragment.setArguments(args);
            //fragment.reSetUriAndId(movieDetailUri,movie_id);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie_container,fragment,MOVIE_DETAIL_FRAGMENT_TAG)
                    .commit();
        }
        else{
            Intent intent = new Intent(this,DetailActivity.class);
            intent.setData(movieDetailUri);
            Bundle bundle = new Bundle();
            bundle.putLong(DetailFragment.DETAIL_MOVIE_ID,movie_id);
            intent.putExtra(DetailFragment.DETAIL_MOVIE_ID,bundle);
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
