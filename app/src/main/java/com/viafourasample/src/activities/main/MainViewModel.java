package com.viafourasample.src.activities.main;

import com.viafourasdk.src.ViafouraSDK;
import com.viafourasdk.src.services.auth.VFAuthService;

public class MainViewModel {
    private VFAuthService auth = ViafouraSDK.auth();

    public void getAuthState(VFAuthService.UserLoginStatusCallback callback){
        auth.getUserLoginStatus(callback);
    }
    public void logout(){
        auth.logout();
    }
}
