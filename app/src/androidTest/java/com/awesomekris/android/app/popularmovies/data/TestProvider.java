package com.awesomekris.android.app.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

/**
 * Created by kris on 16/8/8.
 */
public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie Info table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Video table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void deleteAllRecordsFromDB() {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(MovieContract.ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.TrailerEntry.TABLE_NAME, null, null);
        db.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void deleteAllRecords() {
        deleteAllRecordsFromProvider();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }

    public void testProviderRegistry(){

        PackageManager pm = mContext.getPackageManager();

        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());

        try {
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            assertEquals("Error: MovieProvider registered with authority:" + providerInfo.authority +
            " instead of authority: " + MovieContract.CONTENT_AUTHORITY, providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(), false);
        }

    }

    public void testGetType() {

        String type;
        // .../movie
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.awesomekris.android.sunshine.popularmovies/movie
        assertEquals("Error: the MovieEntry CONTENT_URI should return MovieEntry.CONTENT_DIR_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // .../movie/1
        long _id = 1;
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildMovieItemUriFromId(_id));
        assertEquals("Error: the MovieEntry CONTENT_URI with _id should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        // .../movie/favorite
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildFavoriteMoviesUri());
        assertEquals("Error: the MovieEntry CONTENT_URI with favorite path should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // .../movie/popular
        type = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildPopularMoviesUri());
        assertEquals("Error: the MovieEntry CONTENT_URI with popular path should return MovieEntry.CONTENT_DIR_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // .../movie/top_rated
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildTopRatedMoviesUri());
        assertEquals("Error: the MovieEntry CONTENT_URI with top_rated path should return MovieEntry.CONTENT_DIR_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, type);

        // .../movie/detail/1
        type = mContext.getContentResolver().getType(
                MovieContract.MovieEntry.buildDetailMovieItemUri(_id));
        assertEquals("Error: the MovieEntry CONTENT_URI with detail path should return MovieEntry.CONTENT_ITEM_TYPE",
                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);


        // .../movie/search?movie_id=200111
//        long movie_id = 200111;
//        type = mContext.getContentResolver().getType(
//                MovieContract.MovieEntry.buildMovieSearchByMovieIdUri(movie_id));
//        assertEquals("Error: the MovieEntry CONTENT_URI with movie_id should return MovieEntry.CONTENT_ITEM_TYPE",
//                MovieContract.MovieEntry.CONTENT_ITEM_TYPE, type);

        // .../trailer
        type = mContext.getContentResolver().getType(MovieContract.TrailerEntry.CONTENT_URI);
        assertEquals("Error: the TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_DIR_TYPE",
                MovieContract.TrailerEntry.CONTENT_TYPE, type);

        // .../trailer/123
        long trailer_id = 123;
        type = mContext.getContentResolver().getType(
                MovieContract.TrailerEntry.buildTrailerItemUriFromId(trailer_id));
        assertEquals("Error: the TrailerEntry CONTENT_URI with trailer_id should return TrailerEntry.CONTENT_ITEM_TYPE",
                MovieContract.TrailerEntry.CONTENT_ITEM_TYPE, type);

        // .../trailer/search?movie_id = 1231
//        long trailer_movie_id = 23123;
//        type = mContext.getContentResolver().getType(
//                MovieContract.TrailerEntry.buildTrailerSearchByfMovieIdUri(trailer_movie_id));
//        assertEquals("Error: the TrailerEntry CONTENT_URI with movie_id should return TrailerEntry.CONTENT_DIR_TYPE",
//                MovieContract.TrailerEntry.CONTENT_TYPE, type);

        // .../review
        type = mContext.getContentResolver().getType(MovieContract.ReviewEntry.CONTENT_URI);
        assertEquals("Error: the ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_DIR_TYPE",
                MovieContract.ReviewEntry.CONTENT_TYPE, type);

        // .../review/123
        long review_id = 123;
        type = mContext.getContentResolver().getType(
                MovieContract.ReviewEntry.buildReviewItemUriFromId(review_id));
        assertEquals("Error: the ReviewEntry CONTENT_URI with reviewer_id should return ReviewEntry.CONTENT_ITEM_TYPE",
                MovieContract.ReviewEntry.CONTENT_ITEM_TYPE, type);

        // .../review/search?movie_id = 1231
//        long review_movie_id = 23123;
//        type = mContext.getContentResolver().getType(
//                MovieContract.ReviewEntry.buildReviewSearchByMovieId(review_movie_id));
//        assertEquals("Error: the ReviewEntry CONTENT_URI with movie_id should return ReviewEntry.CONTENT_DIR_TYPE",
//                MovieContract.ReviewEntry.CONTENT_TYPE, type);

    }

    public void testBasicMovieQuery() {
        // insert our test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues dummyMovieValue = TestUtilities.createDummyMovieValue();

        long movieId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, dummyMovieValue);
        assertTrue("Unable to Insert MovieEntry into the Database", movieId != -1);

        db.close();

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQuery", movieCursor, dummyMovieValue);
    }


//    public void testBasicTrailerQueries() {
//        // insert our test records into the database
//        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues dummyMovieValue = TestUtilities.createDummyMovieValue();
//        long movieId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, dummyMovieValue);
//        assertTrue("Unable to Insert MovieEntry into the Database", movieId != -1);
//
//        ContentValues dummyTrailerValue = TestUtilities.createDummyTrailerValue(movieId);
//        long trailerId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, dummyTrailerValue);
//        assertTrue("Unable to Insert TrailerEntry into the Database", trailerId != -1);
//
//        // Test the basic content provider query
//        Cursor trailerCursor = mContext.getContentResolver().query(
//                MovieContract.TrailerEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//        // Make sure we get the correct cursor out of the database
//        TestUtilities.validateCursor("testBasicTrailerQueries", trailerCursor, dummyTrailerValue);
//    }

//    public void testBasicReviewQueries() {
//        // insert our test records into the database
//        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
//        SQLiteDatabase db = dbHelper.getWritableDatabase();
//
//        ContentValues dummyMovieValue = TestUtilities.createDummyMovieValue();
//        long movieId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, dummyMovieValue);
//        assertTrue("Unable to Insert MovieEntry into the Database", movieId != -1);
//
//        ContentValues dummyReviewValue = TestUtilities.createDummyReviewValue(movieId);
//        long reviewId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, dummyReviewValue);
//        assertTrue("Unable to Insert ReviewEntry into the Database", reviewId != -1);
//
//        // Test the basic content provider query
//        Cursor reviewCursor = mContext.getContentResolver().query(
//                MovieContract.ReviewEntry.CONTENT_URI,
//                null,
//                null,
//                null,
//                null
//        );
//
//        // Make sure we get the correct cursor out of the database
//        TestUtilities.validateCursor("testBasicReviewQueries", reviewCursor, dummyReviewValue);
//    }
//
//    public void testInsertReadProvider() {
//        ContentValues testValues = TestUtilities.createDummyMovieValue();
//
//        // Register a content observer for our insert.  This time, directly with the content resolver
//        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
//        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);
//
//        // Did our content observer get called?  Students:  If this fails, your insert location
//        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        long movieRowId = ContentUris.parseId(movieUri);
//
//        // Verify we got a row back.
//        assertTrue(movieRowId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = mContext.getContentResolver().query(
//                MovieContract.MovieEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating movieEntry.",
//                cursor, testValues);
//
//
//        // Fantastic.  Now that we have a movie, add some trailer!
//        ContentValues trailerValues = TestUtilities.createDummyTrailerValue(movieRowId);
//        // The TestContentObserver is a one-shot class
//        tco = TestUtilities.getTestContentObserver();
//
//        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, tco);
//
//        Uri trailerInsertUri = mContext.getContentResolver()
//                .insert(MovieContract.TrailerEntry.CONTENT_URI, trailerValues);
//        assertTrue("insert trailer row unsuccessfully", trailerInsertUri != null);
//
//        // Did our content observer get called?  Students:  If this fails, your insert weather
//        // in your ContentProvider isn't calling
//        // getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        // A cursor is your primary interface to the query results.
//        Cursor trailerCursor = mContext.getContentResolver().query(
//                MovieContract.TrailerEntry.CONTENT_URI,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // columns to group by
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
//                trailerCursor, trailerValues);
//
//
//        // Fantastic.  Now that we have a movie, add some review!
//        ContentValues reviewValues = TestUtilities.createDummyReviewValue(movieRowId);
//        // The TestContentObserver is a one-shot class
//        tco = TestUtilities.getTestContentObserver();
//
//        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);
//
//        Uri reviewInsertUri = mContext.getContentResolver()
//                .insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
//        assertTrue("insert review row unsuccessfully", reviewInsertUri != null);
//
//        // Did our content observer get called?  Students:  If this fails, your insert weather
//        // in your ContentProvider isn't calling
//        // getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        // A cursor is your primary interface to the query results.
//        Cursor reviewCursor = mContext.getContentResolver().query(
//                MovieContract.ReviewEntry.CONTENT_URI,  // Table to Query
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // columns to group by
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
//                reviewCursor, reviewValues);
//
//    }

//
//
//
//    public void testUpdateMovie() {
//        // Create a new map of values, where column names are the keys
//        ContentValues values = TestUtilities.createDummyMovieValue();
//
//        Uri movieUri = mContext.getContentResolver().
//                insert(MovieContract.MovieEntry.CONTENT_URI, values);
//        long movieRowId = ContentUris.parseId(movieUri);
//
//        // Verify we got a row back.
//        assertTrue(movieRowId != -1);
//        Log.d(LOG_TAG, "New row id: " + movieRowId);
//
//        ContentValues updatedValues = new ContentValues(values);
//        updatedValues.put(MovieContract.MovieEntry._ID, movieRowId);
//        updatedValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Zootopia");
//
//        // Create a cursor with observer to make sure that the content provider is notifying
//        // the observers as expected
//        Cursor locationCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//
//        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
//        locationCursor.registerContentObserver(tco);
//
//        int count = mContext.getContentResolver().update(
//                MovieContract.MovieEntry.CONTENT_URI, updatedValues, MovieContract.MovieEntry._ID + "= ?",
//                new String[]{Long.toString(movieRowId)});
//        assertEquals(count, 1);
//
//        // Test to make sure our observer is called.  If not, we throw an assertion.
//        //
//        // Students: If your code is failing here, it means that your content provider
//        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//
//        locationCursor.unregisterContentObserver(tco);
//        locationCursor.close();
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = mContext.getContentResolver().query(
//                MovieContract.MovieEntry.CONTENT_URI,
//                null,   // projection
//                MovieContract.MovieEntry._ID + " = " + movieRowId,
//                null,   // Values for the "where" clause
//                null    // sort order
//        );
//
//        TestUtilities.validateCursor("testUpdateLocation.  Error validating movie entry update.",
//                cursor, updatedValues);
//
//        cursor.close();
//    }
//
//    public void testDeleteRecords() {
//        testInsertReadProvider();
//
//        // Register a content observer for our location delete.
//        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);
//
//        // Register a content observer for our weather delete.
//        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, trailerObserver);
//
//        // Register a content observer for our weather delete.
//        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);
//
//        deleteAllRecordsFromProvider();
//
//        // Students: If either of these fail, you most-likely are not calling the
//        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
//        // delete.  (only if the insertReadProvider is succeeding)
//        movieObserver.waitForNotificationOrFail();
//        trailerObserver.waitForNotificationOrFail();
//        reviewObserver.waitForNotificationOrFail();
//
//        mContext.getContentResolver().unregisterContentObserver(movieObserver);
//        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
//        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
//    }

//    public void testBulkInsert() {
//        // first, let's create a location value
//        ContentValues dummyMovieValue = TestUtilities.createDummyMovieValue();
//        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, dummyMovieValue);
//        long movieId = ContentUris.parseId(movieUri);
//
//        // Verify we got a row back.
//        //assertTrue(movieId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = mContext.getContentResolver().query(
//                MovieContract.MovieEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//
//
//        TestUtilities.validateCursor("testBulkInsert. Error validating MovieEntry.",
//                cursor, dummyMovieValue);
//
//        // Now we can bulkInsert some movies, trailer and reviews.
//
//        ContentValues[] movieContentValues = TestUtilities.createBulkInsertMovieValues();
//        ContentValues[] reviewContentValues = TestUtilities.createBulkInsertReviewValues(movieId);
//        ContentValues[] trailerContentValues = TestUtilities.createBulkInsertTrailerValues(movieId);
//
//        // Register a content observer for our bulk insert.
//        // Register a content observer for our location delete.
//        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);
//
//        // Register a content observer for our weather delete.
//        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, trailerObserver);
//
//        // Register a content observer for our weather delete.
//        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);
//
//        int insertMovieCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieContentValues);
//        int insertTrailerCount = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, trailerContentValues);
//        int insertReviewCount = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, reviewContentValues);
//
//        // Students:  If this fails, it means that you most-likely are not calling the
//        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
//        // ContentProvider method.
//        movieObserver.waitForNotificationOrFail();
//        trailerObserver.waitForNotificationOrFail();
//        reviewObserver.waitForNotificationOrFail();
//
//        mContext.getContentResolver().unregisterContentObserver(movieObserver);
//        mContext.getContentResolver().unregisterContentObserver(trailerObserver);
//        mContext.getContentResolver().unregisterContentObserver(reviewObserver);
//
//        assertEquals(insertMovieCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);
//        assertEquals(insertTrailerCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);
//        assertEquals(insertReviewCount, TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);
//
//        // A cursor is your primary interface to the query results.
//        Cursor movieCursor = mContext.getContentResolver().query(
//                MovieContract.MovieEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // sort order
//        );
//
//        // A cursor is your primary interface to the query results.
//        Cursor trailerCursor = mContext.getContentResolver().query(
//                MovieContract.TrailerEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // sort order
//        );
//
//        // A cursor is your primary interface to the query results.
//        Cursor reviewCursor = mContext.getContentResolver().query(
//                MovieContract.ReviewEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null // sort order
//        );
//
//        // we should have as many records in the database as we've inserted + the first we inserted
//        assertEquals(movieCursor.getCount(), TestUtilities.BULK_INSERT_RECORDS_TO_INSERT +1);
//        assertEquals(trailerCursor.getCount(), TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);
//        assertEquals(reviewCursor.getCount(), TestUtilities.BULK_INSERT_RECORDS_TO_INSERT);
//
//
//        // and let's make sure they match the ones we created
//        movieCursor.moveToFirst();
//        TestUtilities.validateCurrentRecord("testBulkInsert. Error validating MovieEntry[0]",
//                movieCursor, dummyMovieValue);
//        movieCursor.moveToNext();
//
//        for (int i = 0; i < TestUtilities.BULK_INSERT_RECORDS_TO_INSERT; i++, movieCursor.moveToNext()) {
//            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + (i + 1),
//                    movieCursor, movieContentValues[i]);
//        }
//        movieCursor.close();
//
//        trailerCursor.moveToFirst();
//        for (int i = 0; i < TestUtilities.BULK_INSERT_RECORDS_TO_INSERT; i++, trailerCursor.moveToNext()) {
//            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating trailerEntry " + (i + 1),
//                    trailerCursor, trailerContentValues[i]);
//        }
//        trailerCursor.close();
//
//        reviewCursor.moveToFirst();
//        for (int i = 0; i < TestUtilities.BULK_INSERT_RECORDS_TO_INSERT; i++, reviewCursor.moveToNext()) {
//            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating reviewEntry " + (i + 1),
//                    reviewCursor, reviewContentValues[i]);
//        }
//        reviewCursor.close();
//
//    }

    }

