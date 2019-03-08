package com.lbconsulting.divelogfirebase.ui.activities;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.lbconsulting.divelogfirebase.BuildConfig;

import timber.log.Timber;

/**
 * This class initializes application wide resources
 */
public class DiveLogApp extends Application {

    public DiveLogApp() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            // initiate Timber
            Timber.plant(new Timber.DebugTree() {
                             @Override
                             protected String createStackElementTag(StackTraceElement element) {
                                 return "Timber: " + super.createStackElementTag(element) + ":" + element.getLineNumber();
                             }
                         }
            );
        }

        try {
            // Calls to setPersistenceEnabled() must be made before any other
            // usage of FirebaseDatabase instance.
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
//            FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
        } catch (Exception e) {
            Timber.w("onCreate(): Exception: %s.", e.getMessage());
        }
        Timber.i("onCreate() complete.");
    }
}