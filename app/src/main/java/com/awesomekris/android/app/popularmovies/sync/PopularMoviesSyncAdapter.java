package com.awesomekris.android.app.popularmovies.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.awesomekris.android.app.popularmovies.BuildConfig;
import com.awesomekris.android.app.popularmovies.R;
import com.awesomekris.android.app.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by kris on 16/8/20.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    public static final String SYNC_EXTRAS_MOVIE_ID = "sync_movie_id";
    public static final String SYNC_EXTRAS_SORT_TYPE = "sync_sort_type";

//    public static final String MOVIE_POSTER = "poster";
//    public static final String MOVIE_REVIEW = "review";
//    public static final String MOVIE_TRAILER = "trailer";

    private String[] mMovieIdList;


    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG,"Starting sync");
        //String sortType;
        //String sortQuery;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseJsonStr = null;

        String BASE_URL = "http://api.themoviedb.org/3/movie";
        String API_KEY = "api_key";

        String [] posterPath = {"popular","top_rated"};
        String TRAILER = "videos";
        String REVIEW = "reviews";


        if(extras.getLong(SYNC_EXTRAS_MOVIE_ID) != 0) {

            //mMovieId = Long.toString(extras.getLong(SYNC_EXTRAS_MOVIE_ID));
        }

        if(extras.getString(SYNC_EXTRAS_SORT_TYPE) != null) {
            //sortQuery = extras.getString(SYNC_EXTRAS_SORT_TYPE);
        }

        String [][] idArray = new String [2][];
        int count = 0;
        for(String path : posterPath){

            try {
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(path)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();

                Log.v(LOG_TAG, "Build Uri:" + buildUri);

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }

                responseJsonStr = buffer.toString();
                getMovieFromJsonStr(responseJsonStr);
                idArray[count] = mMovieIdList;
                count ++;

            }catch (IOException e){

                Log.e(LOG_TAG, "Error ", e);

            }catch(JSONException e){

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            }finally {

                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream",e);
                    }
                }
            }
        }

        for(String id : idArray[0]){

            try {
                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(id)
                            .appendPath(TRAILER)
                            .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();

                Log.v(LOG_TAG, "Build Uri:" + buildUri);

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }

                responseJsonStr = buffer.toString();
                getTrailerStringFromJsonStr(responseJsonStr);

            }catch (IOException e){

                Log.e(LOG_TAG, "Error ", e);

            }catch(JSONException e){

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            }finally {

                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream",e);
                    }
                }
            }
        }

        for(String id : idArray[1]){

            try {

                Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(id)
                        .appendPath(REVIEW)
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();

                Log.v(LOG_TAG, "Build Uri:" + buildUri);

                URL url = new URL(buildUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");

                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return;
                }

                responseJsonStr = buffer.toString();
                getReviewStringFromJsonStr(responseJsonStr);

            }catch (IOException e){

                Log.e(LOG_TAG, "Error ", e);

            }catch(JSONException e){

                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();

            }finally {

                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
                if(reader != null){
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream",e);
                    }
                }
            }
        }

        return;

    }

    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PopularMoviesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    private void getMovieFromJsonStr(String movieJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";

        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        //final String TMDB_DURATION = "duration";
        final String TMDB_ID = "id";
        //final String TMDB_FAVORITE = "favorite";
        final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            int resultNumber = movieArray.length();
            String[] resultStrs = new String[resultNumber - 1];

            for (int i = 0; i < resultNumber - 1; i++) {
                String poster_path;
                String title;
                String popularity;
                String overview;
                String release_date;
                //String duration;
                String id;
                String vote_average;
                int favorite;

                JSONObject jsonObject = movieArray.getJSONObject(i);
                poster_path = jsonObject.getString(TMDB_POSTER_PATH);
                title = jsonObject.getString(TMDB_TITLE);
                popularity = jsonObject.getString(TMDB_POPULARITY);
                overview = jsonObject.getString(TMDB_OVERVIEW);
                release_date = jsonObject.getString(TMDB_RELEASE_DATE);
                //duration = jsonObject.getString(TMDB_DURATION);
                id = jsonObject.getString(TMDB_ID);
                vote_average = jsonObject.getString(TMDB_VOTE_AVERAGE);
                favorite = 0;

                resultStrs[i] = MOVIE_IMAGE_BASE_URL + poster_path;

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                //movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, duration);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, resultStrs[i]);
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                mMovieIdList = new String[cVVector.size()];
                long id = 0;
                int updatedrow = -1;
                int count = 0;
                int size = 0;
                String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

                for (ContentValues contentValues : cvArray) {

                    id = contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                    mMovieIdList[size] = Long.toString(id);
                    String[] selectionArgs = {Long.toString(id)};
                    Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, selection, selectionArgs, null);
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        int favoriteIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
                        int favorite = cursor.getInt(favoriteIndex);
                        contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);
                        updatedrow = getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                        if (updatedrow != -1) {
                            count++;
                            Log.d(LOG_TAG, "UpdateMovieTask Complete. " + updatedrow + " Updated");

                        } else {
                            Log.d(LOG_TAG, "UpdateMovieTask Complete. " + "No Rows" + " Updated");
                        }
                    }
                    cursor.close();
                    size ++;
                }
                if(count != cVVector.size()){
                    inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }

            }
            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }



    private void getTrailerStringFromJsonStr(String trailerJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_MOVIE_ID = "id";

        try{

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TMDB_RESULTS);


            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            int resultNumber = trailerArray.length();
//            String[] resultStrs = new String[resultNumber - 1];

            for(int i = 0; i <= resultNumber - 1; i++){

                String movie_id;
                String key;
                String name;


                JSONObject jsonObject = trailerArray.getJSONObject(i);

                movie_id = trailerJson.getString(TMDB_MOVIE_ID);
                key = jsonObject.getString(TMDB_KEY);
                name = jsonObject.getString(TMDB_NAME);


                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie_id);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY,key);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE,name);


                cVVector.add(trailerValues);

            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                String key;
                int updatedrow = -1;
                int count = 0;
                String selection = MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " = ? ";

                for (ContentValues contentValues : cvArray) {

                    key = contentValues.getAsString(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
                    String[] selectionArgs = {key};
                    Cursor cursor = getContext().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null, selection, selectionArgs, null);
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        updatedrow = getContext().getContentResolver().update(MovieContract.TrailerEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                        if (updatedrow != -1) {
                            count ++;
                            Log.d(LOG_TAG, "UpdateTrailerTask Complete. " + updatedrow + " Updated");

                        } else {
                            Log.d(LOG_TAG, "UpdateTrailerTask Complete. " + "No Rows" + " Updated");
                        }
                    }
                    cursor.close();

                }

                if(count != cVVector.size()) {
                    inserted = getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
                }
            }

            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");


        }catch (JSONException e){

            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }


    private void getReviewStringFromJsonStr(String reviewJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_REVIEW_ID = "id";
        final String TMDB_MOVIE_ID = "id";

        try{

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

            int resultNumber = reviewArray.length();

            for(int i = 0; i <= resultNumber - 1; i++){

                String movie_id;
                String author;
                String content;
                String review_id;

                JSONObject jsonObject = reviewArray.getJSONObject(i);

                movie_id = reviewJson.getString(TMDB_MOVIE_ID);
                author = jsonObject.getString(TMDB_AUTHOR);
                content = jsonObject.getString(TMDB_CONTENT);
                review_id = jsonObject.getString(TMDB_REVIEW_ID);


                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,movie_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,author);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,content);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID,review_id);

                cVVector.add(reviewValues);

            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);

                String key;
                int updatedrow = -1;
                int count = 0;
                String selection = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ";

                for (ContentValues contentValues : cvArray) {

                    key = contentValues.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
                    String[] selectionArgs = {key};
                    Cursor cursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, selection, selectionArgs, null);
                    if (cursor.getCount() != 0) {
                        cursor.moveToFirst();
                        updatedrow = getContext().getContentResolver().update(MovieContract.ReviewEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                        if (updatedrow != -1) {
                            count ++;
                            Log.d(LOG_TAG, "UpdateReviewTask Complete. " + updatedrow + " Updated");

                        } else {
                            Log.d(LOG_TAG, "UpdateReviewTask Complete. " + "No Rows" + " Updated");
                        }
                    }
                    cursor.close();
                }

                if(count != cVVector.size()) {
                    inserted = getContext().getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
                }
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");

        }catch (JSONException e){

            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

}
