package com.viafourasample.src.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.ads.MobileAds;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.fragments.home.HomeFragment;
import com.viafourasdk.src.services.auth.AuthService;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel = new MainViewModel();
    private Menu toolbarMenu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fragment fragment = new HomeFragment();
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.home_fragment_holder, fragment);
        ft.commit();

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.home));
    }

    @Override
    protected void onResume() {
        super.onResume();

        getAuthState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        toolbarMenu = menu;
        getAuthState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_main_auth){
            viewModel.getAuthState(new AuthService.UserLoginStatusCallback() {
                @Override
                public void onSuccess(boolean userIsLoggedIn) {
                    if(userIsLoggedIn){
                        viewModel.logout();
                        toolbarMenu.findItem(R.id.menu_main_auth).setTitle("Log in");
                    } else {
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAuthState(){
        viewModel.getAuthState(new AuthService.UserLoginStatusCallback() {
            @Override
            public void onSuccess(boolean userIsLoggedIn) {
                if(toolbarMenu != null){
                    if(userIsLoggedIn){
                        toolbarMenu.findItem(R.id.menu_main_auth).setTitle("Log out");
                    } else {
                        toolbarMenu.findItem(R.id.menu_main_auth).setTitle("Log in");
                    }
                }
            }
        });
    }
}