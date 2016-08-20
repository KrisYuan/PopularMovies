package com.awesomekris.android.app.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.awesomekris.android.app.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;


/**
 * Created by kris on 16/8/7.
 */
public class TestUtilities extends AndroidTestCase {

    static public final int BULK_INSERT_RECORDS_TO_INSERT = 10;


    static void validateCursor (String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    //checks if all expectedValues are in the the cursor current position. Not the other way around
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for(Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }

    }

    static ContentValues createDummyMovieValue() {
        ContentValues row = new ContentValues();
        //row.put(MovieContract.MovieEntry._ID, 32310);
        //row.put(MovieContract.MovieEntry.COLUMN_DURATION, 110);
        row.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        row.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "great great great");
        row.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 45.323);
        row.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/dfsadfasdfsadf.jpg");
        row.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 5.5);
        row.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-11-11");
        row.put(MovieContract.MovieEntry.COLUMN_TITLE, "dummy movie");
        row.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 200111);
        return row;
    }

    static ContentValues createDummyTrailerValue(long movieId) {
        ContentValues row = new ContentValues();
        //row.put(MovieContract.TrailerEntry._ID, 1);
        row.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
        row.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, "trailer 1");
        row.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "sskujnhdjdjsjs");
        return row;
    }

    static ContentValues createDummyReviewValue(long movieId) {
        ContentValues row = new ContentValues();
        //row.put(MovieContract.ReviewEntry._ID, 1);
        row.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
        row.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "walker");
        row.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "good and fun");
        return row;
    }

    static ContentValues[] createBulkInsertMovieValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues row = new ContentValues();
            //row.put(MovieContract.MovieEntry._ID, 32313 + i);
            //row.put(MovieContract.MovieEntry.COLUMN_DURATION, 110 + i);
            row.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
            row.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "great great great");
            row.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 45.323 + i);
            row.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/dfsadfasdfsadf.jpg");
            row.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, 5.5 + i / 5);
            row.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016-11-11");
            row.put(MovieContract.MovieEntry.COLUMN_TITLE, "dummy movie");
            row.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, 200111 + i);
            returnContentValues[i] = row;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertTrailerValues(long movieId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues row = new ContentValues();
            //row.put(MovieContract.TrailerEntry._ID, 32310 + i);
            row.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            row.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, "trailer " + i);
            row.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, "sskujnhdjdjsjs" + i);
            returnContentValues[i] = row;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues(long movieId) {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues row = new ContentValues();
            //row.put(MovieContract.ReviewEntry._ID, 32310 + i);
            row.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            row.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, "walker" + i);
            row.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, "good and fun" + i);
            returnContentValues[i] = row;
        }
        return returnContentValues;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }


}
