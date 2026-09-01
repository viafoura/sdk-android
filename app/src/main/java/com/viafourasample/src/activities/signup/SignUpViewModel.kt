package com.viafourasample.src.activities.signup

import com.viafourasdk.src.ViafouraSDK
import com.viafourasdk.src.model.network.authentication.signup.SignUpResponse
import com.viafourasdk.src.model.network.error.NetworkError
import com.viafourasdk.src.services.auth.VFAuthService

class SignUpViewModel {
    private val auth: VFAuthService = ViafouraSDK.auth()

    fun signup(name: String, email: String, password: String, callback: SignUpCallback) {
        auth.signup(name, email, password, object : VFAuthService.SignUpCallback {
            override fun onSuccess(loginResponse: SignUpResponse) {
                callback.onSuccess()
            }

            override fun onError(err: NetworkError) {
                callback.onError()
            }
        })
    }

    interface SignUpCallback {
        fun onSuccess()
        fun onError()
    }
}
