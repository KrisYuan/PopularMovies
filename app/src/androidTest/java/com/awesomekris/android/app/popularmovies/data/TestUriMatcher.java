//package com.awesomekris.android.app.popularmovies.data;
//
//import android.content.UriMatcher;
//import android.net.Uri;
//import android.test.AndroidTestCase;
//
//import com.awesomekris.android.app.popularmovies.data.MovieContract;
//import com.awesomekris.android.app.popularmovies.data.MovieProvider;
//
///**
// * Created by kris on 16/8/7.
// */
//public class TestUriMatcher extends AndroidTestCase {
//    private static final long TEST_MOVIE_ID_QUERY = 200111;
//    private static final long TEST_REVIEW_ID_QUERY = 300111;
//    private static final long TEST_VIDEO_ID_QUERY = 400111;
//
//    private static final Uri TEST_MOVIE_INFO_DIR = MovieContract.MovieEntry.CONTENT_URI;
//    private static final Uri TEST_REVIEW_WITH_ID = MovieContract.ReviewEntry.buildReviewItemUriFromId(TEST_REVIEW_ID_QUERY);
//    private static final Uri TEST_TRAILER_WITH_ID = MovieContract.TrailerEntry.buildTrailerItemUriFromId(TEST_VIDEO_ID_QUERY);
//    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
//    private static final Uri TEST_VIDEO_DIR = MovieContract.TrailerEntry.CONTENT_URI;
//
//    public void testUriMatcher(){
//
//        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
//
//        assertEquals("Error : The MOVIE_INFO URI was matched incorrectly.", testMatcher.match(TEST_MOVIE_INFO_DIR), MovieProvider.MOVIE_INFO);
//        assertEquals("Error : The REVIEW_WITH_REVIEW_ID URI was matched incorrectly.", testMatcher.match(TEST_REVIEW_WITH_ID), MovieProvider.REVIEW_ITEM);
//        assertEquals("Error : The VIDEO_WITH_VIDEO_ID URI was matched incorrectly.", testMatcher.match(TEST_TRAILER_WITH_ID), MovieProvider.TRAILER_ITEM);
//        assertEquals("Error : The REVIEW URI was matched incorrectly.", testMatcher.match(TEST_REVIEW_DIR), MovieProvider.REVIEW);
//        assertEquals("Error : The VIDEO URI was matched incorrectly.", testMatcher.match(TEST_VIDEO_DIR), MovieProvider.TRAILER);
//
//
//    }
//
//}
