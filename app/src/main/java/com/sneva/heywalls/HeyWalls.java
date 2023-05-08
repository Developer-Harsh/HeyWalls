package com.sneva.heywalls;

import android.app.Application;

import com.sneva.easyprefs.EasyPrefs;

public class HeyWalls extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        EasyPrefs.initialize(getApplicationContext());
    }
}
