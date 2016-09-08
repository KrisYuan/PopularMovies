package com.awesomekris.android.app.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by kris on 16/8/5.
 */
public class MovieContract {

    /*
    * content://com.awesomekris.android.popularmovies/movie
    *
    * TABLE_NAME:
    *               movie
    *
    * COLUMN_NAME:
    *               _ID
    *               title
    *               poster_path
    *               release_date
    *               overview
    *               popularity
    *               vote_average
    *               duration
    *               movie_id
    *               favorite
    *
    * */

    //Authority is the package name for the app
    public static final String CONTENT_AUTHORITY = "com.awesomekris.android.app.popularmovies";
    //Use CONTENT_AUTHORITY to create the base of all URIs which can be used to contact the content provider
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    //Looking for movie info, review or vedio
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_REVIEW = "review";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_DETAIL = "detail";

    private static final String LOG_TAG = MovieContract.class.getSimpleName();

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();
        //return dir for movie
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_MOVIE;
        //return item for movie
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" +PATH_MOVIE;
        //table name
        public static final String TABLE_NAME = "movie";
        //Column Name
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_VOTE_AVERAGE = "vote_average";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_FAVORITE = "favorite";

        public static final String[] MOVIE_COLUMNS = {
                _ID,
                COLUMN_TITLE,
                COLUMN_POSTER_PATH,
                COLUMN_RELEASE_DATE,
                COLUMN_OVERVIEW,
                COLUMN_POPULARITY,
                COLUMN_VOTE_AVERAGE,
                //COLUMN_DURATION,
                COLUMN_MOVIE_ID,
                COLUMN_FAVORITE,

        };

        public static final int COLUMN_INDEX_ID = 0;
        public static final int COLUMN_INDEX_TITLE = 1;
        public static final int COLUMN_INDEX_POSTER_PATH = 2;
        public static final int COLUMN_INDEX_RELEASE_DATE = 3;
        public static final int COLUMN_INDEX_OVERVIEW = 4;
        public static final int COLUMN_INDEX_POPULARITY = 5;
        public static final int COLUMN_INDEX_VOTE_AVERAGE = 6;
        //public static final int COLUMN_INDEX_DURATION = 7;
        public static final int COLUMN_INDEX_MOVIE_ID = 7;
        public static final int COLUMN_INDEX_FAVORITE = 8;


        /*
        dir
        .../movie
        .../movie/popular
        .../movie/top_rated
        .../movie/favorite


        item
        .../movie/id
        .../movie/search?movie_id = movie_id
        .../movie/detail/movie_id
         */

        public static final String PATH_POPULAR = "popular";
        public static final String PATH_TOP_RATED = "top_rated";
        public static final String PATH_FAVORITE = "favorite";
        public static final String PATH_SEARCH = "search";
        public static final String PATH_DETAIL = "detail";

        // .../movie/id
        public static Uri buildMovieItemUriFromId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        //.../movie/detail/movie_id
        public static Uri buildDetailMovieItemUri(long id) {
            Uri detailUriPath = CONTENT_URI.buildUpon().appendPath(PATH_DETAIL).build();
            return ContentUris.withAppendedId(detailUriPath, id);
        }
        // .../movie/popular
        public static Uri buildPopularMoviesUri() {
            return MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_POPULAR).build();
        }
        // .../movie/top_rated
        public static Uri buildTopRatedMoviesUri() {
            return MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_TOP_RATED).build();
        }
        // .../movie/favorite
        public static Uri buildFavoriteMoviesUri() {
            return MovieContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(PATH_FAVORITE).build();
        }

        // .../movie/search?movie_id = movie_id
        public static Uri buildMovieSearchByMovieIdUri(long movie_id) {
            return MovieContract.MovieEntry.CONTENT_URI.buildUpon()
                    .appendPath(PATH_SEARCH).appendQueryParameter(COLUMN_MOVIE_ID,Long.toString(movie_id)).build();
        }

//        //.../movie/detail/id
//        public static Uri buildDetailMovieUriFromId(long id) {
//            Uri detailUriPath = CONTENT_URI.buildUpon().appendPath(PATH_DETAIL).build();
//            return ContentUris.withAppendedId(detailUriPath, id);
//        }


        public static Uri buildSortMovieUri(String sortKey) {
            switch (sortKey) {
                case PATH_POPULAR:
                    return buildPopularMoviesUri();
                case PATH_TOP_RATED:
                    return buildTopRatedMoviesUri();
                case PATH_FAVORITE:
                    return buildFavoriteMoviesUri();
                default:
                    Log.e(LOG_TAG, "building sort movie uri, unsupported sort key received");
                    return null;
            }
        }

        //parse  .../movie/movie_id
        public static long getIdFromUri(Uri uri){
            return Long.parseLong(uri.getLastPathSegment());
        }

        //parse  .../movie/search?movie_id = movie_id
        public static long getMovieIdFromUri(Uri uri) {
            System.out.print(uri.getQueryParameter(COLUMN_MOVIE_ID));
            return Long.parseLong(uri.getQueryParameter(COLUMN_MOVIE_ID));
        }

    }

    /*
    * content://com.awesomekris.android.popularmovies/movieInfo
    *
    * TABLE_NAME:
    *               trailer
    *
    * COLUMN_NAME:
    *               _ID
    *               movie_id (foreign key)
    *               youtube_key
    *               trailer_title
    * */

    public static final class TrailerEntry implements BaseColumns {


        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String PATH_SEARCH = "search";

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_MOVIE_ID = "trailer_movie_id";
        //in form of "hEJnMQG9ev8"
        //use "https://www.youtube.com/watch?v=hEJnMQG9ev8" to open trailer
        public static final String COLUMN_YOUTUBE_KEY = "youtube_key";
        public static final String COLUMN_TRAILER_TITLE = "trailer_title";

        public static final String[] TRAILER_COLUMNS = {
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_TRAILER_TITLE,
                COLUMN_YOUTUBE_KEY
        };

        public static final int COLUMN_INDEX_ID = 0;
        public static final int COLUMN_INDEX_MOVIE_ID = 1;
        public static final int COLUMN_INDEX_TRAILER_TITLE = 2;
        public static final int COLUMN_INDEX_YOUTUBE_KEY = 3;

        /*
        dir
        .../trailer
        .../trailer/search?movie_id=movie_id

        item
        .../trailer/id

         */

        // .../trailer/id
        public static Uri buildTrailerItemUriFromId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        // .../trailer/search?movie_id = movie_id
        public static Uri buildTrailerSearchByfMovieIdUri(long movie_id) {
            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SEARCH)
                    .appendQueryParameter(COLUMN_MOVIE_ID, Long.toString(movie_id)).build();
        }

        //parse  .../trailer/id
        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
        //parse  .../trailer/search?movie_id = movie_id
        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getQueryParameter(COLUMN_MOVIE_ID));
        }


    }


    /*
    * content://com.awesomekris.android.popularmovies/review
    *
    * TABLE_NAME:
    *               review
    *
    * COLUMN_NAME:
    *               _ID
    *               movie_id (foreign key)
    *               author
    *               content
    *               review_id
    * */

    public static final class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String PATH_SEARCH = "search";

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_MOVIE_ID = "review_movie_id";
        public static final String COLUMN_REVIEW_AUTHOR = "review_author";
        public static final String COLUMN_REVIEW_CONTENT = "review_content";
        public static final String COLUMN_REVIEW_ID = "review_id";

        public static final String[] REVIEW_COLUMNS = {
                _ID,
                COLUMN_MOVIE_ID,
                COLUMN_REVIEW_AUTHOR,
                COLUMN_REVIEW_CONTENT,
                COLUMN_REVIEW_ID
        };

        public static final int COLUMN_INDEX_ID = 0;
        public static final int COLUMN_INDEX_MOVIE_ID = 1;
        public static final int COLUMN_INDEX_REVIEW_AUTHOR = 2;
        public static final int COLUMN_INDEX_REVIEW_CONTENT = 3;
        public static final int COLUMN_INDEX_REVIEW_ID = 4;

        /*
        dir
        .../review
        .../review/search?movie_id=movie_id

        item
        .../review/id

         */

        // .../review/id
        public static Uri buildReviewItemUriFromId(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
        // .../review/search?movie_id=movie_id
        public static Uri buildReviewSearchByMovieId( long movie_id) {

            return CONTENT_URI.buildUpon()
                    .appendPath(PATH_SEARCH)
                    .appendQueryParameter(COLUMN_MOVIE_ID,Long.toString(movie_id))
                    .build();
        }


        //parse  .../review/id
        public static long getIdFromUri(Uri uri) {
            return Long.parseLong(uri.getLastPathSegment());
        }
        //parse  .../review/search?movie_id = movie_id
        public static long getMovieIdFromUri(Uri uri) {
            return Long.parseLong(uri.getQueryParameter(COLUMN_MOVIE_ID));
        }

    }

}
