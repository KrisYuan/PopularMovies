package com.awesomekris.android.app.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by kris on 16/8/7.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {

        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        assertTrue("Error: This means that the database has not been created correctly", c.moveToFirst());

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: This means that we were unable to query the database for table informatioin.", tableNameHashSet.isEmpty());

        //MOVIE TABLE
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE);
        //movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_DURATION);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);

        int movieColumnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(movieColumnNameIndex);
            movieColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns", movieColumnHashSet.isEmpty());

        //REVIEW TABLE
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        final HashSet<String> reviewColumnHashSet = new HashSet<String>();
        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT);


        int reviewColumnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(reviewColumnNameIndex);
            reviewColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns", reviewColumnHashSet.isEmpty());

        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")", null);
        assertTrue("Error: This means that we were unable to query the database for table information.", c.moveToFirst());

        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY);

        int trailerColumnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(trailerColumnNameIndex);
            trailerColumnHashSet.remove(columnName);
        }while(c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required location entry columns", trailerColumnHashSet.isEmpty());

        c.close();
        db.close();

    }

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        deleteTheDatabase();
    }



    public void testInsertMovieRow() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues dummyMovieRow = TestUtilities.createDummyMovieValue();
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, dummyMovieRow);
        assertTrue(rowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null, //all columns
                null, //all rows
                null, //
                null, //
                null, //
                null //
        );

        // Move the cursor to a valid database row
        assertTrue("Error: cursor is error or empty", cursor.moveToFirst());


        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error: Location Query Validation Fail", cursor, dummyMovieRow);
        assertFalse("Error: The database has more than one record", cursor.moveToNext());

        cursor.close();
        db.close();
    }

    public void testInsertTrailerTable() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues dummyMovieRow = TestUtilities.createDummyMovieValue();
        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, dummyMovieRow);
        assertTrue(movieId != -1);

        ContentValues dummyTrailerRow = TestUtilities.createDummyTrailerValue(200111);
        long trailerId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, dummyTrailerRow);
        assertTrue(trailerId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MovieContract.TrailerEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("testInsertReadDb trailerEntry failed to validate",
                cursor, dummyTrailerRow);
        assertFalse(cursor.moveToNext());

        cursor.close();
        db.close();

    }

    public void testInsertReviewTable() {
        SQLiteDatabase db = new MovieDbHelper(mContext).getWritableDatabase();

        ContentValues dummyMovieRow = TestUtilities.createDummyMovieValue();
        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, dummyMovieRow);
        assertTrue(movieId != -1);

        ContentValues dummyReviewRow = TestUtilities.createDummyReviewValue(200111);
        long reviewId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, dummyReviewRow);

        assertTrue(reviewId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MovieContract.ReviewEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null
        );
        assertTrue(cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("testInsertReadDb reviewEntry failed to validate",
                cursor, dummyReviewRow);
        assertFalse(cursor.moveToNext());

        cursor.close();
        db.close();

    }



}
