package com.example.gnbtransactions;

import android.app.Application;
import android.content.Context;

import com.example.gnbtransactions.repo.TransactionRepository;

import timber.log.Timber;

public class App extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = getApplicationContext();

        if (BuildConfig.DEBUG)
            Timber.plant(new Timber.DebugTree());

        Timber.d("App has initialized...");
    }
    public static Context getContext(){
        return mContext;
    }
}