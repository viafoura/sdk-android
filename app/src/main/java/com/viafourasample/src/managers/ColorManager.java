package com.viafourasample.src.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.viafourasample.src.model.SettingKeys;

public class ColorManager {
    public static boolean isDarkMode(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingKeys.darkMode, false);
    }
}
