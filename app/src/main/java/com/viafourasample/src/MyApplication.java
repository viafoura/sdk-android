package com.viafourasample.src;

import android.app.Application;
import android.preference.PreferenceManager;

import com.google.android.gms.ads.MobileAds;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.ViafouraSDK;

public class MyApplication extends Application {
    private static MyApplication singleton;

    public MyApplication getInstance(){
        return singleton;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        singleton = this;

        MobileAds.initialize(this);

        String siteUUID = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SettingKeys.siteUUID, SettingKeys.DEFAULT_SITE_UUID);
        String siteDomain = PreferenceManager.getDefaultSharedPreferences(this)
                .getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN);

        ViafouraSDK.initialize(getApplicationContext(), siteUUID, siteDomain);
        ViafouraSDK.isLoggingEnabled = true;
    }
}