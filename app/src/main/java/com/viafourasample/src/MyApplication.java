package com.viafourasample.src;

import android.app.Application;
import com.google.android.gms.ads.MobileAds;
import com.loginradius.androidsdk.helper.LoginRadiusSDK;
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

        LoginRadiusSDK.Initialize init = new LoginRadiusSDK.Initialize();
        init.setApiKey("fb763410-afd1-4646-8232-5a5a4f0d9bae");
        init.setSiteName("viafoura-login");


        OneSignal.initWithContext(this, "85a688d0-16cd-495a-99a9-1407fd349364");

        ViafouraSDK.initialize(getApplicationContext(), "00000000-0000-4000-8000-c8cddfd7b365", "viafoura-mobile-demo.vercel.app");
        ViafouraSDK.isLoggingEnabled = true;
    }
}