package com.viafourasample.src.activities.splash;

import android.os.CountDownTimer;

public class SplashActivityViewModel {
    private SplashActivityCallback splashActivityCallback;

    public SplashActivityViewModel() {
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                splashActivityCallback.onLoadingCompleted();
            }

        }.start();
    }

    public void setSplashActivityCallback(SplashActivityCallback splashActivityCallback) {
        this.splashActivityCallback = splashActivityCallback;
    }

    public interface SplashActivityCallback {
        void onLoadingCompleted();
    }
}
