package com.viafourasample.src.activities.settings;

import com.viafourasample.src.model.Setting;
import com.viafourasample.src.model.SettingKeys;

import java.util.ArrayList;
import java.util.List;

public class SettingsViewModel {
    public List<Setting> settingList = new ArrayList<>();

    public SettingsViewModel(){
        settingList.add(new Setting("Use comments container on fullscreen", SettingKeys.commentsContainerFullscreen));
        settingList.add(new Setting("Show trending articles", SettingKeys.showTrendingArticles));
        settingList.add(new Setting("Dark mode", SettingKeys.darkMode));
        settingList.add(new Setting("Show notification bell on top bar", SettingKeys.showNotificationBellTopBar));
    }
}
