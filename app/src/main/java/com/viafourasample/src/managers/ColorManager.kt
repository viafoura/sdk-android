package com.viafourasample.src.managers

import android.content.Context
import android.preference.PreferenceManager
import com.viafourasample.src.model.SettingKeys

object ColorManager {
    fun isDarkMode(context: Context): Boolean =
        PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(SettingKeys.darkMode, false)
}
