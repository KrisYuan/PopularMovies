package com.awesomekris.android.app.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by kris on 16/8/7.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final long TEST_MOVIE_ID = 200111;
    private static final long TEST_REVIEW_ID =  300111;
    private static final long TEST_VIDEO_ID = 400111;

    public void testBuildMovieInfo() {

        Uri movieUri = MovieContract.MovieEntry.buildMovieItemUriFromId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildMovieUriFromId in " + "MovieEntry." + movieUri);
        assertEquals("Error: Movie ID not properly appended to the end of the Uri", TEST_MOVIE_ID, Long.parseLong(movieUri.getLastPathSegment()));
        assertEquals("Error: MovieId Uri doesn't match our expected result", movieUri.toString(),"content://com.awesomekris.android.app.popularmovies/movie/200111");

        Uri movieByMovieIdUri = MovieContract.MovieEntry.buildMovieSearchByMovieIdUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildMovieUriFromId in " + "MovieEntry." + movieByMovieIdUri);
        assertEquals("Error: Movie by Movie ID not properly appended to the end of the Uri", TEST_MOVIE_ID, Long.parseLong(movieByMovieIdUri.getQueryParameters(MovieContract.MovieEntry.COLUMN_MOVIE_ID).get(0)));
        assertEquals("Error: Movie by Movie ID Uri doesn't match our expected result", movieByMovieIdUri.toString(),"content://com.awesomekris.android.app.popularmovies/movie/search?movie_id=200111");



    }

    public void testBuildReview() {

        Uri reviewUri = MovieContract.ReviewEntry.buildReviewItemUriFromId(TEST_REVIEW_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildReviewUriFromId in " + "ReviewEntry." + reviewUri);
        assertEquals("Error: Review ID not properly appended to the end of the Uri", TEST_REVIEW_ID, Long.parseLong(reviewUri.getLastPathSegment()));
        assertEquals("Error: ReviewId Uri doesn't match our expected result", reviewUri.toString(),"content://com.awesomekris.android.app.popularmovies/review/300111");

        Uri reviewByMovieIdUri = MovieContract.ReviewEntry.buildReviewSearchByMovieId(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildReviewUriFromId in " + "ReviewEntry." + reviewByMovieIdUri);
        assertEquals("Error: Review by Movie ID not properly appended to the end of the Uri", TEST_MOVIE_ID, Long.parseLong(reviewByMovieIdUri.getQueryParameters(MovieContract.ReviewEntry.COLUMN_MOVIE_ID).get(0)));
        assertEquals("Error: Review by Movie ID Uri doesn't match our expected result", reviewByMovieIdUri.toString(),"content://com.awesomekris.android.app.popularmovies/review/search?review_movie_id=200111");




    }

    public void testBuildVideo() {

        Uri videoUri = MovieContract.TrailerEntry.buildTrailerItemUriFromId(TEST_VIDEO_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildVideoUri in " + "VideoEntry." + videoUri);
        assertEquals("Error: Video ID not properly appended to the end of the Uri", TEST_VIDEO_ID, Long.parseLong(videoUri.getLastPathSegment()));
        assertEquals("Error: VideoId Uri doesn't match our expected result", videoUri.toString(),"content://com.awesomekris.android.app.popularmovies/trailer/400111");

        Uri videoByMovieInfoUri = MovieContract.TrailerEntry.buildTrailerSearchByfMovieIdUri(TEST_MOVIE_ID);
        assertNotNull("Error: Null Uri returned. You must fill-in buildReviewUri in " + "VideoEntry." + videoByMovieInfoUri);
        assertEquals("Error: Video by Movie ID not properly appended to the end of the Uri", TEST_MOVIE_ID, Long.parseLong(videoByMovieInfoUri.getQueryParameters(MovieContract.TrailerEntry.COLUMN_MOVIE_ID).get(0)));
        assertEquals("Error: Video by Movie ID Uri doesn't match our expected result", videoByMovieInfoUri.toString(),"content://com.awesomekris.android.app.popularmovies/trailer/search?trailer_movie_id=200111");


    }

}
