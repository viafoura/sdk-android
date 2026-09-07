package com.viafourasample.src.activities.splash

import android.os.CountDownTimer

class SplashActivityViewModel {
    var splashActivityCallback: SplashActivityCallback? = null

    init {
        object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                splashActivityCallback?.onLoadingCompleted()
            }
        }.start()
    }

    fun interface SplashActivityCallback {
        fun onLoadingCompleted()
    }
}
