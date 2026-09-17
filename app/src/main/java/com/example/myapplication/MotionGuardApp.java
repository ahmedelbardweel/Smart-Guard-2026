package com.example.myapplication;

import android.app.Application;

public class MotionGuardApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ThemeHelper.applyNightMode(this);
        ReportNotifier.ensureChannel(this);
        if (ReportPrefs.isEnabled(this)) {
            ReportScheduler.ensureScheduled(this);
        }
    }
}
