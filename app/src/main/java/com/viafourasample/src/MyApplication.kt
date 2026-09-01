package com.viafourasample.src

import android.app.Application
import android.preference.PreferenceManager
import com.google.android.gms.ads.MobileAds
import com.viafourasample.src.model.SettingKeys
import com.viafourasdk.src.ViafouraSDK

class MyApplication : Application() {

    fun getInstance(): MyApplication? = singleton

    override fun onCreate() {
        super.onCreate()
        singleton = this

        MobileAds.initialize(this)

        val siteUUID = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(SettingKeys.siteUUID, SettingKeys.DEFAULT_SITE_UUID)
        val siteDomain = PreferenceManager.getDefaultSharedPreferences(this)
            .getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN)

        ViafouraSDK.initialize(applicationContext, siteUUID, siteDomain)
        ViafouraSDK.isLoggingEnabled = true
    }

    companion object {
        private var singleton: MyApplication? = null
    }
}
