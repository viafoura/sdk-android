package com.viafourasample.src.activities.main;

import com.viafourasdk.src.ViafouraSDK;
import com.viafourasdk.src.services.auth.AuthService;

public class MainViewModel {
    private AuthService auth = ViafouraSDK.auth();

    public void getAuthState(AuthService.UserLoginStatusCallback callback){
        auth.getUserLoginStatus(callback);
    }
    public void logout(){
        auth.logout();
    }
}
