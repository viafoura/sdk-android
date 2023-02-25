package com.viafourasample.src.activities.main;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.settings.SettingsActivity;
import com.viafourasample.src.fragments.home.HomeFragment;
import com.viafourasample.src.fragments.livechat.LiveChatFragment;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasdk.src.services.auth.AuthService;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel = new MainViewModel();
    private Menu toolbarMenu;
    private Fragment homeFragment, liveChatFragment;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        liveChatFragment = new LiveChatFragment();

        setCurrentFragment(true);

        bottomNavigationView = ((BottomNavigationView) findViewById(R.id.home_bottom_navigation));
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.main_chat) {
                    setCurrentFragment(false);
                } else if(item.getItemId() == R.id.main_home) {
                    setCurrentFragment(true);
                }
                return false;
            }
        });

        final Drawable upArrow = getResources().getDrawable(R.drawable.icon_settings);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setCurrentFragment(boolean isHome){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(isHome){
            getSupportActionBar().setTitle(getResources().getString(R.string.home));
            ft.replace(R.id.home_fragment_holder, homeFragment);
        } else{
            getSupportActionBar().setTitle("Live Chat");
            ft.replace(R.id.home_fragment_holder, liveChatFragment);
        }

        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ColorManager.isDarkMode(getApplicationContext())){
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        } else {
            bottomNavigationView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
        }

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
        else if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
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