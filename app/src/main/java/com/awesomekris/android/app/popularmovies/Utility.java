package com.awesomekris.android.app.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.awesomekris.android.app.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kris on 16/8/17.
 */
public class Utility {
    private static String LOG_TAG = Utility.class.getSimpleName();

    public static String getPreferredSort(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }

    public static String getJsonStringFromUrl(URL url) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String responseJsonStr = null;

        /*
        Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(path)
                .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();

        Log.v(LOG_TAG, "Build Uri:" + buildUri);

        URL url = new URL(buildUri.toString());
        */

        try {

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {

                buffer.append(line + "\n");

            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            responseJsonStr = buffer.toString();

        } catch (IOException e) {

            Log.e(LOG_TAG, "Error ", e);

        } finally {

            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }

        return responseJsonStr;

    }

    public static URL buildMovieListUrlWithPath(String path) {
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            final String API_KEY_PARAM = "api_key";

            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(path)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY).build();

            Log.v(LOG_TAG, "Build Uri:" + buildUri);

            return new URL(buildUri.toString());

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "malformed URL in movie list url building function");
            e.printStackTrace();
            return null;
        }
    }

    public static URL buildDetailListUrlWithPath(String path, String id) {
        try {
            final String BASE_URL = "http://api.themoviedb.org/3/movie";
            final String API_KEY_PARAM = "api_key";


            Uri buildUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(id)
                    .appendPath(path)
                    .appendQueryParameter(API_KEY_PARAM, BuildConfig.MOVIE_DB_API_KEY).build();

            Log.v(LOG_TAG, "Build Uri:" + buildUri);

            return new URL(buildUri.toString());

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "malformed URL in movie list url building function");
            e.printStackTrace();
            return null;
        }
    }

    public static List<ContentValues> parseMovieJsonArray(String movieJsonStr) throws JSONException {

        List<ContentValues> allMovieContentValues = new ArrayList<>();

        final String TMDB_RESULTS = "results";
        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_ID = "id";
        final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

        int resultNumber = movieArray.length();
        String[] resultStrs = new String[resultNumber - 1];

        for (int i = 0; i < resultNumber - 1; i++) {
            String poster_path;
            String title;
            String popularity;
            String overview;
            String release_date;
            String id;
            String vote_average;
            int defaultFavoriteValue;

            JSONObject jsonObject = movieArray.getJSONObject(i);
            poster_path = jsonObject.getString(TMDB_POSTER_PATH);
            title = jsonObject.getString(TMDB_TITLE);
            popularity = jsonObject.getString(TMDB_POPULARITY);
            overview = jsonObject.getString(TMDB_OVERVIEW);
            release_date = jsonObject.getString(TMDB_RELEASE_DATE);
            id = jsonObject.getString(TMDB_ID);
            vote_average = jsonObject.getString(TMDB_VOTE_AVERAGE);
            defaultFavoriteValue = 0;

            resultStrs[i] = MOVIE_IMAGE_BASE_URL + poster_path;

            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
            movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, resultStrs[i]);
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, defaultFavoriteValue);

            allMovieContentValues.add(movieValues);
        }

        return allMovieContentValues;
    }

    public static List<ContentValues> parseReviewJsonArray(String reviewJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";
        final String TMDB_REVIEW_ID = "id";
        final String TMDB_MOVIE_ID = "id";

        JSONObject reviewJson = new JSONObject(reviewJsonStr);
        JSONArray reviewArray = reviewJson.getJSONArray(TMDB_RESULTS);

        List<ContentValues> allReviewContentValues = new ArrayList<ContentValues>();

        int resultNumber = reviewArray.length();

        for (int i = 0; i <= resultNumber - 1; i++) {

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

            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movie_id);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR, author);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT, content);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, review_id);

            allReviewContentValues.add(reviewValues);

        }

        return allReviewContentValues;
    }

    public static List<ContentValues> parseTrailerJsonArray(String trailerJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";
        final String TMDB_MOVIE_ID = "id";

        JSONObject trailerJson = new JSONObject(trailerJsonStr);
        JSONArray trailerArray = trailerJson.getJSONArray(TMDB_RESULTS);


        List<ContentValues> allTrailerContentValues = new ArrayList<ContentValues>();

        int resultNumber = trailerArray.length();

        for (int i = 0; i <= resultNumber - 1; i++) {

            String movie_id;
            String key;
            String name;


            JSONObject jsonObject = trailerArray.getJSONObject(i);

            movie_id = trailerJson.getString(TMDB_MOVIE_ID);
            key = jsonObject.getString(TMDB_KEY);
            name = jsonObject.getString(TMDB_NAME);


            ContentValues trailerValues = new ContentValues();

            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie_id);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY, key);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE, name);


            allTrailerContentValues.add(trailerValues);

        }

        return allTrailerContentValues;
    }

}
