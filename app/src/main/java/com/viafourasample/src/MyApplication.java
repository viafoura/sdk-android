package com.viafourasample.src;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import com.onesignal.OneSignal;
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

        OneSignal.initWithContext(this, "8add46ba-1535-4c77-8c97-4faccd2cd7e5");

        ViafouraSDK.initialize(getApplicationContext(), "00000000-0000-4000-8000-c8cddfd7b365", "viafoura-mobile-demo.vercel.app");
        ViafouraSDK.isLoggingEnabled = true;
    }
}