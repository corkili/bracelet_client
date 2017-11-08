package org.client.bracelet.ui;

import android.app.Application;

import com.beardedhen.androidbootstrap.TypefaceProvider;

/**
 * Created by 李浩然 on 2017/11/8.
 */

public class BraceletApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        TypefaceProvider.registerDefaultIconSets();
    }
}
