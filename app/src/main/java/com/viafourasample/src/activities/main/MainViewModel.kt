package com.viafourasample.src.activities.main

import com.viafourasdk.src.ViafouraSDK
import com.viafourasdk.src.services.auth.VFAuthService

class MainViewModel {
    private val auth: VFAuthService = ViafouraSDK.auth()

    fun getAuthState(callback: VFAuthService.UserLoginStatusCallback) {
        auth.getUserLoginStatus(callback)
    }

    fun logout() {
        auth.logout()
    }
}
