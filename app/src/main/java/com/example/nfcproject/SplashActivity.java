package com.example.nfcproject;


import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;

public class SplashActivity extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // register the util to remove splash screen after loading
        registerActivityLifecycleCallbacks(new SplashScreenHelper());
    }
}

class SplashScreenHelper implements Application.ActivityLifecycleCallbacks {

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        try {
            ActivityInfo activityInfo = activity.getPackageManager()
                    .getActivityInfo(activity.getComponentName(), PackageManager.GET_META_DATA);

            Bundle metaData = activityInfo.metaData;

            int theme;
            if (metaData != null) {
                theme = metaData.getInt("theme", R.style.AppTheme);
            } else {
                theme = R.style.AppTheme;
            }

            activity.setTheme(theme);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onActivityStarted(Activity activity) {
        // do nothing
    }

    @Override
    public void onActivityResumed(Activity activity) {
        // do nothing
    }

    @Override
    public void onActivityPaused(Activity activity) {
        // do nothing
    }

    @Override
    public void onActivityStopped(Activity activity) {
        // do nothing
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        // do nothing
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        // do nothing
    }
}
