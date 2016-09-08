package com.awesomekris.android.app.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kris on 16/8/5.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    private static final String LOG_TAG = MovieDbHelper.class.getSimpleName();

    public MovieDbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_INFO_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME
                + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POSTER_PATH + " TEXT  NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_POPULARITY + " REAL NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL, " +
//                MovieContract.MovieEntry.COLUMN_DURATION + " INTEGER NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " REAL UNIQUE NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_FAVORITE + " INTEGER NOT NULL " +
                ");";

        final String SQL_CREATE_TRAILER_TABLE = "CREATE TABLE " + MovieContract.TrailerEntry.TABLE_NAME + " (" +
                MovieContract.TrailerEntry._ID + " INTEGER PRIMARY KEY NOT NULL," +
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE + " TEXT NOT NULL, " +
                MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY + " TEXT UNIQUE NOT NULL "
                + ");";
//                " FOREIGN KEY (" + MovieContract.TrailerEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
//                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ");";
//                + " UNIQUE (" + MovieContract.TrailerEntry.COLUMN_VIDEO_ID + ", " +
//                MovieContract.TrailerEntry.COLUMN_MOVIE_INFO_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " (" +
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " REAL NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT UNIQUE NOT NULL "
                + ");";
//                " FOREIGN KEY (" + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + ") REFERENCES " +
//                MovieContract.MovieEntry.TABLE_NAME + " (" + MovieContract.MovieEntry.COLUMN_MOVIE_ID + ")";
//                + " UNIQUE (" + MovieContract.ReviewEntry.COLUMN_REVIEW_ID + ", " +
//                MovieContract.ReviewEntry.COLUMN_MOVIE_INFO_KEY + ") ON CONFLICT REPLACE);";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_INFO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_TRAILER_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.TrailerEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
