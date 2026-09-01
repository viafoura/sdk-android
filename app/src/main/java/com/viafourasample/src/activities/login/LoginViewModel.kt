package com.viafourasample.src.activities.login

import com.viafourasdk.src.ViafouraSDK
import com.viafourasdk.src.model.network.authentication.login.LoginResponse
import com.viafourasdk.src.model.network.error.NetworkError
import com.viafourasdk.src.services.auth.VFAuthService

class LoginViewModel {
    private val auth: VFAuthService = ViafouraSDK.auth()

    fun login(email: String, password: String, callback: LoginCallback) {
        auth.login(email, password, object : VFAuthService.LoginCallback {
            override fun onSuccess(loginResponse: LoginResponse) {
                callback.onSuccess()
            }

            override fun onError(err: NetworkError) {
                callback.onError(err.message)
            }
        })
    }

    fun resetPassword(email: String, callback: PasswordResetCallback) {
        auth.passwordReset(email, object : VFAuthService.PasswordResetCallback {
            override fun onSuccess() {
                callback.onSuccess()
            }

            override fun onError(err: NetworkError) {
                callback.onError()
            }
        })
    }

    interface LoginCallback {
        fun onSuccess()
        fun onError(errorMessage: String?)
    }

    interface PasswordResetCallback {
        fun onSuccess()
        fun onError()
    }
}
