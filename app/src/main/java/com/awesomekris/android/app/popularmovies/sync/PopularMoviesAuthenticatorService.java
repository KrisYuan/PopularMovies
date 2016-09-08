package com.awesomekris.android.app.popularmovies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by kris on 16/8/20.
 */
public class PopularMoviesAuthenticatorService extends Service {

    private PopularMoviesAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new PopularMoviesAuthenticator(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
