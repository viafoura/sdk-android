package com.viafourasample.src.activities.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {
    private SplashActivityViewModel viewModel = new SplashActivityViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        viewModel.setSplashActivityCallback(new SplashActivityViewModel.SplashActivityCallback() {
            @Override
            public void onLoadingCompleted() {
                goToMain();
            }
        });
    }

    private void goToMain(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }
}