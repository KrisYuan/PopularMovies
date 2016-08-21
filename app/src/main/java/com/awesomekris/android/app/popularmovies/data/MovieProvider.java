package com.awesomekris.android.app.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by kris on 16/8/5.
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE = 100;
    static final int MOVIE_ITEM = 101;
    static final int MOVIE_POPULAR = 102;
    static final int MOVIE_TOP_RATED = 103;
    static final int MOVIE_FAVORITE = 104;
    static final int MOVIE_SEARCH_BY_MOVIE_ID = 105;

    final static int TRAILER = 200;
    final static int TRAILER_ITEM = 201;
    final static int TRAILER_SEARCH_BY_MOVIE_ID = 202;

    final static int REVIEW = 300;
    final static int REVIEW_ITEM = 301;
    final static int REVIEW_SEARCH_BY_MOVIE_ID = 302;

    final static int MOVIE_DETAIL = 401;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mMovieHelper;

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        // .../movie
        matcher.addURI(authority, MovieContract.PATH_MOVIE, MOVIE);
        // .../movie/id
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/#", MOVIE_ITEM);
        // .../movie/popular
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.MovieEntry.PATH_POPULAR, MOVIE_POPULAR);
        // .../movie/top_rated
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.MovieEntry.PATH_TOP_RATED, MOVIE_TOP_RATED);
        // .../movie/favorite
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.MovieEntry.PATH_FAVORITE, MOVIE_FAVORITE);
        // .../movie/search?movie_id=movie_id
        matcher.addURI(authority, MovieContract.PATH_MOVIE + "/" + MovieContract.MovieEntry.PATH_SEARCH, MOVIE_SEARCH_BY_MOVIE_ID);


        // .../trailer
        matcher.addURI(authority, MovieContract.PATH_TRAILER, TRAILER);
        // .../trailer/id
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/#", TRAILER_ITEM);
        // .../trailer/search?trailer_movie_id=moive_id
        matcher.addURI(authority, MovieContract.PATH_TRAILER + "/" + MovieContract.TrailerEntry.PATH_SEARCH, TRAILER_SEARCH_BY_MOVIE_ID);

        // .../review
        matcher.addURI(authority, MovieContract.PATH_REVIEW, REVIEW);
        // .../review/id
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/#", REVIEW_ITEM);
        // .../review/search?review_movie_id=moive_id
        matcher.addURI(authority, MovieContract.PATH_REVIEW + "/" + MovieContract.ReviewEntry.PATH_SEARCH, REVIEW_SEARCH_BY_MOVIE_ID);

        // .../movie/detail/movie_id
        matcher.addURI(authority,
                MovieContract.PATH_MOVIE + "/" + MovieContract.MovieEntry.PATH_DETAIL + "/#",
                MOVIE_DETAIL);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        mMovieHelper = new MovieDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_ITEM:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIE_POPULAR:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_TOP_RATED:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_FAVORITE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_SEARCH_BY_MOVIE_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case TRAILER_ITEM:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
            case TRAILER_SEARCH_BY_MOVIE_ID:
                return MovieContract.TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case REVIEW_ITEM:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
            case REVIEW_SEARCH_BY_MOVIE_ID:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    //movie._ID = ?
    private static final String sMovieByIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "."
                    + MovieContract.MovieEntry._ID + " = ? ";

    //popularity sort order
    private static final String popularSortOrder =
            MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC LIMIT 20";

    //top_rated sort order
    private static final String rateSortOrder =
            MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " DESC LIMIT 20";

    //favorite sort order
    private static final String favoriteSortOrder =
            MovieContract.MovieEntry.COLUMN_FAVORITE + " DESC";

    private static final String sMovieByFavoriteSelection =
            MovieContract.MovieEntry.TABLE_NAME +
            "." + MovieContract.MovieEntry.COLUMN_FAVORITE + " =? ";

    //movie.movie_id = ?
    private static final String sMovieByMovieIdSelection =
            MovieContract.MovieEntry.TABLE_NAME + "."
                    + MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    //review.movie_id = ?
    private static final String sReviewByMovieIdSelection =
            MovieContract.ReviewEntry.TABLE_NAME + "."
            + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ? ";


    //trailer.movie_id = ?
    private static final String sTrailerByMovieIdSelection =
            MovieContract.TrailerEntry.TABLE_NAME + "."
                    + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ? ";


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        SQLiteDatabase db = mMovieHelper.getReadableDatabase();

        switch (sUriMatcher.match(uri)){
            case MOVIE: {
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_ITEM:
            {
                Long _id = MovieContract.MovieEntry.getIdFromUri(uri);
                String movieItemSelection = sMovieByIdSelection;
                String [] movieItenSelectionArgs = new String[]{Long.toString(_id)};
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        movieItemSelection,
                        movieItenSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            case MOVIE_POPULAR:
            {
                retCursor =  db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        popularSortOrder
                );
                break;

            }
            case MOVIE_TOP_RATED:
            {
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        rateSortOrder
                );
                break;
            }
            case MOVIE_FAVORITE:
            {
                String favoriteSelection = sMovieByFavoriteSelection;
                String[] favoriteSelectionArgs = new String[]{"1"};
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        favoriteSelection,
                        favoriteSelectionArgs,
                        null,
                        null,
                        favoriteSortOrder
                );
                break;
            }
            case MOVIE_SEARCH_BY_MOVIE_ID:
                Long movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);
                String movieSelection = sMovieByMovieIdSelection;
                String[] movieSelectionArgs = new String[]{Long.toString(movieId)};
                retCursor = db.query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        movieSelection,
                        movieSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER:
                retCursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case TRAILER_SEARCH_BY_MOVIE_ID:
                Long trailerMovieId = MovieContract.TrailerEntry.getMovieIdFromUri(uri);
                String trailerMovieSelection = sTrailerByMovieIdSelection;
                String[] trailerMovieSelectionArgs = new String[]{Long.toString(trailerMovieId)};
                retCursor = db.query(
                        MovieContract.TrailerEntry.TABLE_NAME,
                        projection,
                        trailerMovieSelection,
                        trailerMovieSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW:
                retCursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case REVIEW_SEARCH_BY_MOVIE_ID:
                Long reviewMovieId = MovieContract.ReviewEntry.getMovieIdFromUri(uri);
                String reviewMovieSelection = sReviewByMovieIdSelection;
                String[] reviewMovieSelectionArgs = new String[]{Long.toString(reviewMovieId)};
                retCursor = db.query(
                        MovieContract.ReviewEntry.TABLE_NAME,
                        projection,
                        reviewMovieSelection,
                        reviewMovieSelectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_DETAIL:
                Long movie_id = MovieContract.MovieEntry.getIdFromUri(uri);
                Cursor cursorDetail = query(MovieContract.MovieEntry.buildMovieSearchByMovieIdUri(movie_id), MovieContract.MovieEntry.MOVIE_COLUMNS, null, null, null);
                Cursor cursorTrailer = query(MovieContract.TrailerEntry.buildTrailerSearchByfMovieIdUri(movie_id), MovieContract.TrailerEntry.TRAILER_COLUMNS, null, null, null);
                Cursor cursorReview = query(MovieContract.ReviewEntry.buildReviewSearchByMovieId(movie_id), MovieContract.ReviewEntry.REVIEW_COLUMNS, null, null, null);
                Cursor[] cursors = new Cursor[]{cursorDetail, cursorTrailer, cursorReview};
                retCursor = new MergeCursor(cursors);
                break;
            default:
                   throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }




    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri retUri;

        switch (match) {
            case MOVIE: {
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MovieContract.MovieEntry.buildMovieItemUriFromId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TRAILER: {
                long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MovieContract.TrailerEntry.buildTrailerItemUriFromId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case REVIEW: {
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    retUri = MovieContract.ReviewEntry.buildReviewItemUriFromId(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return retUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsDeleted = 0;

        if (null == selection) selection = "1";

        switch (match) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknow Uri: " + uri);
        }

        if (rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsUpdated = 0;

        switch (match){
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME,values,selection,selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknow Uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri,null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mMovieHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int movieReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            movieReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return movieReturnCount;
            case REVIEW:
                db.beginTransaction();
                int reviewReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            reviewReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return reviewReturnCount;
            case TRAILER:
                db.beginTransaction();
                int trailerReturnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            trailerReturnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return trailerReturnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mMovieHelper.close();
        super.shutdown();
    }


}

