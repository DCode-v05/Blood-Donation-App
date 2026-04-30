package com.lifesaver.blooddonation;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.lifesaver.blooddonation.managers.NotificationHelper;

public class LifeSaverApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        NotificationHelper.ensureChannel(this);
    }
}
