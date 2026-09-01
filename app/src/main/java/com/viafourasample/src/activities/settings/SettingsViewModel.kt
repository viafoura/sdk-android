package com.viafourasample.src.activities.settings

import com.viafourasample.src.model.Setting
import com.viafourasample.src.model.SettingKeys

class SettingsViewModel {
    val settingList: List<Setting> = listOf(
        Setting("Use comments container on fullscreen", SettingKeys.commentsContainerFullscreen),
        Setting("Dark mode", SettingKeys.darkMode),
        Setting("Custom container IDs", SettingKeys.customContainerIDs),
        Setting("Show notification bell on top bar", SettingKeys.showNotificationBellTopBar)
    )
}
