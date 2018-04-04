package com.keepcalm.blomo;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Keep Calm on 3/20/2018.
 */

public class OfflineCapability extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
