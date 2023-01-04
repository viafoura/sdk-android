package com.viafourasample.src.activities.signup;

import com.viafourasdk.src.ViafouraSDK;
import com.viafourasdk.src.model.network.authentication.signup.SignUpResponse;
import com.viafourasdk.src.model.network.error.NetworkError;
import com.viafourasdk.src.services.auth.AuthService;

public class SignUpViewModel {
    private AuthService auth = ViafouraSDK.auth();

    public void signup(String name, String email, String password, SignUpCallback callback){
        auth.signup(name, email, password, new AuthService.SignUpCallback() {
            @Override
            public void onSuccess(SignUpResponse loginResponse) {
                callback.onSuccess();
            }

            @Override
            public void onError(NetworkError err) {
                callback.onError();
            }
        });
    }

    interface SignUpCallback {
        void onSuccess();
        void onError();
    }
}
