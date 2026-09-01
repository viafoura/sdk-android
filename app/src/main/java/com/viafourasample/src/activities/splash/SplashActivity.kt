package com.viafourasample.src.activities.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.main.MainActivity

class SplashActivity : AppCompatActivity() {
    private val viewModel = SplashActivityViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        viewModel.splashActivityCallback =
            SplashActivityViewModel.SplashActivityCallback { goToMain() }
    }

    private fun goToMain() {
        startActivity(Intent(applicationContext, MainActivity::class.java))
    }
}
