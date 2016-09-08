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
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.awesomekris.android.app.popularmovies.R;
import com.awesomekris.android.app.popularmovies.Utility;
import com.awesomekris.android.app.popularmovies.data.MovieContract;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by kris on 16/8/20.
 */
public class PopularMoviesSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String LOG_TAG = PopularMoviesSyncAdapter.class.getSimpleName();
    //public static final int SYNC_INTERVAL = 30; //test
    public static final int SYNC_INTERVAL = 3 * 60 * 60; // 3 hours
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public PopularMoviesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        Log.d(LOG_TAG, "Starting sync");

        final String[] posterPath = {"popular", "top_rated"};
        final String TRAILER = "videos";
        final String REVIEW = "reviews";

        Set<String> allMovieIdList = new HashSet<>();
        ArrayList<String> movieIdList = new ArrayList<>();
        try {
            for (String path : posterPath) {

                URL url = Utility.buildMovieListUrlWithPath(path);
                String movieListJsonResponse = Utility.getJsonStringFromUrl(url);
                movieIdList.clear();
                movieIdList = parseAndInsertMovieJsonStr(movieListJsonResponse);
                allMovieIdList.addAll(movieIdList);

            }

            List<ContentValues> toInsertTrailersOfAllMovies = new ArrayList<ContentValues>();
            List<ContentValues> toInsertReviewsOfAllMovies = new ArrayList<ContentValues>();

            for (String movieId : allMovieIdList) {

                URL trailUrl = Utility.buildDetailListUrlWithPath(TRAILER, movieId);
                String trailerJsonString = Utility.getJsonStringFromUrl(trailUrl);
                List<ContentValues> toInsertTrailers = parseAndUpdateTrailerJsonStr(trailerJsonString);
                if (toInsertTrailers != null) {
                    toInsertTrailersOfAllMovies.addAll(toInsertTrailers);
                }

                URL reviewUrl = Utility.buildDetailListUrlWithPath(REVIEW, movieId);
                String reviewJsonString = Utility.getJsonStringFromUrl(reviewUrl);
                List<ContentValues> toInsertReviews = parseAndUpdateReviewJsonStr(reviewJsonString);
                if (toInsertReviews!=null)
                    toInsertReviewsOfAllMovies.addAll(toInsertReviews);

            }

            int insertedTrailerNum = getContext().getContentResolver().bulkInsert(
                    MovieContract.TrailerEntry.CONTENT_URI,
                    toInsertTrailersOfAllMovies.toArray(new ContentValues[toInsertTrailersOfAllMovies.size()])
            );

            int insertedReviewNum = getContext().getContentResolver().bulkInsert(
                    MovieContract.ReviewEntry.CONTENT_URI,
                    toInsertReviewsOfAllMovies.toArray(new ContentValues[toInsertReviewsOfAllMovies.size()])
            );

            Log.d(LOG_TAG, "FetchDetailTask Complete. "
                    + insertedTrailerNum + " trailers inserted and "
                    + insertedReviewNum + " reviews inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "corrupted json file");
            e.printStackTrace();
        }

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
        if (null == accountManager.getPassword(newAccount)) {

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

    private ArrayList<String> parseAndInsertMovieJsonStr(String movieJsonStr) throws JSONException {

        List<ContentValues> allMovieContentValues = Utility.parseMovieJsonArray(movieJsonStr);

        ArrayList<String> movieIdList = insertMovieContentValuesAndGetMovieIdList(allMovieContentValues);

        return movieIdList;
    }

    private List<ContentValues> parseAndUpdateTrailerJsonStr(String trailerJsonStr) throws JSONException {

        List<ContentValues> allTrailerContentValues = Utility.parseTrailerJsonArray(trailerJsonStr);

        return updateTrailerContentValues(allTrailerContentValues);

    }

    private List<ContentValues> parseAndUpdateReviewJsonStr(String reviewJsonStr) throws JSONException {

        List<ContentValues> allReviewContentValues = Utility.parseReviewJsonArray(reviewJsonStr);

        return updateReviewContentValues(allReviewContentValues);
    }

    public ArrayList<String> insertMovieContentValuesAndGetMovieIdList(List<ContentValues> allMovieContentValues) {

        ArrayList<String> movieIdList = new ArrayList<>();

        if (allMovieContentValues.size() == 0) {
            Log.e(LOG_TAG, "parse movie json has no values");
            return null;
        }

        int insertedNum = 0;
        int updatedNum = 0;
        List<ContentValues> toInsertMovieContentValues = new ArrayList<>();

        for (ContentValues contentValues : allMovieContentValues) {

            long id = contentValues.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
            movieIdList.add(Long.toString(id));
            String selection = MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";
            String[] selectionArgs = {Long.toString(id)};
            Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, selection, selectionArgs, null);
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                int favoriteIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_FAVORITE);
                int favorite = cursor.getInt(favoriteIndex);
                Log.d("Favorite:", Integer.toString(favorite));
                contentValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);
                getContext().getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                updatedNum += 1;
            } else {
                toInsertMovieContentValues.add(contentValues);
            }
            cursor.close();
        }

        insertedNum = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI,
                toInsertMovieContentValues.toArray(new ContentValues[toInsertMovieContentValues.size()]));

        Log.d(LOG_TAG, "FetchMovieTask Complete. "
                + insertedNum + " inserted and "
                + updatedNum + " updated "
                + "of total num " + allMovieContentValues.size());

        return movieIdList;
    }

    public List<ContentValues> updateTrailerContentValues(List<ContentValues> allTrailerContentValues) {

        /*
        @params: all new trailer content values
        @return: to be inserted content values.  The updated content values are not returned
        */

        if (allTrailerContentValues.size() == 0) {
            Log.e(LOG_TAG, "parsed review json has no review");
            return null;
        }
        List<ContentValues> toInsertTrailerContentValues = new ArrayList<>();
        int updatedNum = 0;
        for (ContentValues contentValues : allTrailerContentValues) {

            String key = contentValues.getAsString(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);
            String selection = MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " = ? ";
            String[] selectionArgs = {key};
            Cursor cursor = getContext().getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null, selection, selectionArgs, null);
            if (cursor.getCount() != 0) {
                getContext().getContentResolver().update(MovieContract.TrailerEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                updatedNum += 1;
            } else {
                toInsertTrailerContentValues.add(contentValues);
            }
            cursor.close();
        }

        Log.d(LOG_TAG, updatedNum + "rows updated!");

        return toInsertTrailerContentValues;
//        int insertedNum = getContext().getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI,
//                toInsertTrailerContentValues.toArray(new ContentValues[toInsertTrailerContentValues.size()]));
//
//        Log.d(LOG_TAG, "FetchTrailerTask Complete. "
//                + insertedNum + " inserted and "
//                + updatedNum + " updated "
//                + "of total num " + allTrailerContentValues.size());
    }

    public List<ContentValues> updateReviewContentValues(List<ContentValues> allReviewContentValues) {
        /*
        @params: all new review content values
        @return: to be inserted content values.  The updated content values are not returned
        */
        if (allReviewContentValues.size() == 0) {
            Log.e(LOG_TAG, "parsed review json has no review");
            return null;
        }

        int updatedNum = 0;
        List<ContentValues> toInsertReviewContentValues = new ArrayList<>();

        for (ContentValues contentValues : allReviewContentValues) {

            String selection = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ";

            String key = contentValues.getAsString(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
            String[] selectionArgs = {key};

            Cursor cursor = getContext().getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, selection, selectionArgs, null);

            if (cursor.getCount() != 0) {
                getContext().getContentResolver().update(MovieContract.ReviewEntry.CONTENT_URI, contentValues, selection, selectionArgs);
                updatedNum += 1;

            } else {
                toInsertReviewContentValues.add(contentValues);
            }
            cursor.close();
        }

        Log.d(LOG_TAG, updatedNum + "rows updated!");

        return toInsertReviewContentValues;

    }

}
