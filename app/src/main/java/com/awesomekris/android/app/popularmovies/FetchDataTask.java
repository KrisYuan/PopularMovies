package com.awesomekris.android.app.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
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
import java.net.URL;
import java.util.Vector;

/**
 * Created by kris on 16/8/17.
 */
public class FetchDataTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchDataTask.class.getSimpleName();

    private final Context mContext;
    private String mMovie_id;


    //request type
    public static String mRequestType = "";
    public static final String MOVIE_POSTER = "poster";
    public static final String MOVIE_REVIEW = "review";
    public static final String MOVIE_TRAILER = "trailer";

    //make uri
    private static final String BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String API_KEY = "api_key";
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "top_rated";
    private static final String TRAILER = "videos";
    private static final String REVIEW = "reviews";


    public FetchDataTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(String... params) {

        if (params.length == 0) {
            return null;
        }

        try {
            Uri builtUri = null;
            mRequestType = params[1];
            switch (mRequestType) {
                case MOVIE_POSTER:
                    builtUri = Uri.parse(BASE_URL + params[0]).buildUpon()
                            .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();
                    mMovie_id = null;
                    break;
                case MOVIE_TRAILER:
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath(TRAILER)
                            .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();
                    mMovie_id = params[0];
                    break;
                case MOVIE_REVIEW:
                    builtUri = Uri.parse(BASE_URL).buildUpon()
                            .appendPath(params[0])
                            .appendPath(REVIEW)
                            .appendQueryParameter(API_KEY, BuildConfig.MOVIE_DB_API_KEY).build();
                    mMovie_id = params[0];
                    break;
            }
            Log.v(LOG_TAG, "Build Uri:" + builtUri);


            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String responseJsonStr = null;
            InputStream inputStream;
            StringBuffer buffer;
            try {
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                inputStream = urlConnection.getInputStream();
                buffer = new StringBuffer();

                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                if ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {

                    return null;
                }
                responseJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Movie JSON String: " + responseJsonStr);

                //TODO Parse Response Data
                switch (mRequestType){
                    case MOVIE_POSTER:
                        getMovieFromJsonStr(responseJsonStr);
                        break;
                    case MOVIE_TRAILER:
                        getTrailerStringFromJsonStr(responseJsonStr);
                        break;
                    case MOVIE_REVIEW:
                        getReviewStringFromJsonStr(responseJsonStr);
                }


            } catch (IOException e) {
                Log.e(LOG_TAG, "Error: " + e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error: " + e);
                    }
                }

            }

            return null;
        }catch (Exception e){
            Log.v(LOG_TAG, "Connection Error: " + e);
            return null;
        }
    }


    private void getMovieFromJsonStr(String movieJsonStr) throws JSONException {
        final String TMDB_RESULTS = "results";

        final String TMDB_TITLE = "title";
        final String TMDB_POSTER_PATH = "poster_path";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POPULARITY = "popularity";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        //final String TMDB_DURATION = "duration";
        final String TMDB_ID = "id";
        //final String TMDB_FAVORITE = "favorite";
        final String MOVIE_IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());

            int resultNumber = movieArray.length();
            String[] resultStrs = new String[resultNumber - 1];

            for (int i = 0; i < resultNumber - 1; i++) {
                String poster_path;
                String title;
                String popularity;
                String overview;
                String release_date;
                String duration;
                String id;
                String vote_average;
                int favorite;

                JSONObject jsonObject = movieArray.getJSONObject(i);
                poster_path = jsonObject.getString(TMDB_POSTER_PATH);
                title = jsonObject.getString(TMDB_TITLE);
                popularity = jsonObject.getString(TMDB_POPULARITY);
                overview = jsonObject.getString(TMDB_OVERVIEW);
                release_date = jsonObject.getString(TMDB_RELEASE_DATE);
                //duration = jsonObject.getString(TMDB_DURATION);
                id = jsonObject.getString(TMDB_ID);
                vote_average = jsonObject.getString(TMDB_VOTE_AVERAGE);
                favorite = 0;

                resultStrs[i] = MOVIE_IMAGE_BASE_URL + poster_path;

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, release_date);
                movieValues.put(MovieContract.MovieEntry.COLUMN_VOTE_AVERAGE, vote_average);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                //movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, duration);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, resultStrs[i]);
                movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, favorite);

                cVVector.add(movieValues);
            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }



    private void getTrailerStringFromJsonStr(String trailerJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_NAME = "name";

        try{

            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerArray = trailerJson.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(trailerArray.length());

            int resultNumber = trailerArray.length();
//            String[] resultStrs = new String[resultNumber - 1];

            for(int i = 0; i <= resultNumber - 1; i++){

                String movie_id;
                String key;
                String name;


                JSONObject jsonObject = trailerArray.getJSONObject(i);

                movie_id = mMovie_id;
                key = jsonObject.getString(TMDB_KEY);
                name = jsonObject.getString(TMDB_NAME);


                ContentValues trailerValues = new ContentValues();

                trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movie_id);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_YOUTUBE_KEY,key);
                trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_TITLE,name);


                cVVector.add(trailerValues);

            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchTrailerTask Complete. " + inserted + " Inserted");


        }catch (JSONException e){

            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }


    private void getReviewStringFromJsonStr(String reviewJsonStr) throws JSONException {

        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        try{

            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewArray = reviewJson.getJSONArray(TMDB_RESULTS);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(reviewArray.length());

            int resultNumber = reviewArray.length();

            for(int i = 0; i <= resultNumber - 1; i++){

                String movie_id;
                String author;
                String content;

                JSONObject jsonObject = reviewArray.getJSONObject(i);

                movie_id = mMovie_id;
                author = jsonObject.getString(TMDB_AUTHOR);
                content = jsonObject.getString(TMDB_CONTENT);


                ContentValues reviewValues = new ContentValues();

                reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID,movie_id);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_AUTHOR,author);
                reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_CONTENT,content);

                cVVector.add(reviewValues);

            }

            int inserted = 0;

            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, cvArray);
            }

            Log.d(LOG_TAG, "FetchReviewTask Complete. " + inserted + " Inserted");


        }catch (JSONException e){

            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

}
