package com.viafourasample.src.activities.login;

import com.onesignal.OneSignal;
import com.viafourasdk.src.ViafouraSDK;
import com.viafourasdk.src.model.network.authentication.login.LoginResponse;
import com.viafourasdk.src.model.network.authentication.socialLogin.SocialLoginResponse;
import com.viafourasdk.src.model.network.error.NetworkError;
import com.viafourasdk.src.services.auth.VFAuthService;

public class LoginViewModel {
    private VFAuthService auth = ViafouraSDK.auth();

    public void login(String email, String password, LoginCallback callback){
        auth.login(email, password, new VFAuthService.LoginCallback() {
            @Override
            public void onSuccess(LoginResponse loginResponse) {
                OneSignal.login(String.valueOf(loginResponse.result.id));
                callback.onSuccess();
            }

            @Override
            public void onError(NetworkError err) {
                callback.onError(err.message);
            }
        });
    }

    interface LoginCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public void resetPassword(String email, PasswordResetCallback callback){
        auth.passwordReset(email, new VFAuthService.PasswordResetCallback() {
            @Override
            public void onSuccess() {
                callback.onSuccess();
            }

            @Override
            public void onError(NetworkError err) {
                callback.onError();
            }
        });
    }

    interface PasswordResetCallback {
        void onSuccess();
        void onError();
    }

    public void socialLogin(String token, SocialLoginCallback socialLoginCallback){
        auth.socialLogin(token, new VFAuthService.SocialLoginCallback() {
            @Override
            public void onSuccess(SocialLoginResponse loginResponse) {
                socialLoginCallback.onSuccess();
            }

            @Override
            public void onError(NetworkError err) {
                socialLoginCallback.onError();
            }
        });
    }

    interface SocialLoginCallback {
        void onSuccess();
        void onError();
    }
}
