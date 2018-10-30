package com.physics_2d_demo;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/*
 * @author Stefan Wyszyński 2009 przeniesiona do androida z J2ME
 */
public class PhysicsDemoApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}