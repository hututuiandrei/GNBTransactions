package com.example.gnbtransactions;

import android.app.Application;
import android.content.Context;

import com.example.gnbtransactions.repo.Repository;

import timber.log.Timber;

public class App extends Application {

    private static Context mContext;
    private static Repository repository;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        repository = new Repository(this);

        Timber.d("App has initialized...");
    }
    public static Context getContext(){
        return mContext;
    }

    public static Repository getRepository() {
        return repository;
    }
}