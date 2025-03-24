package com.viafourasample.src.activities.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.activities.settings.SettingsActivity;
import com.viafourasample.src.fragments.home.HomeFragment;
import com.viafourasample.src.fragments.livechat.LiveChatFragment;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.interfaces.NotificationBellClickedInterface;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;
import com.viafourasdk.src.services.auth.VFAuthService;
import com.viafourasdk.src.view.notificationBell.VFNotificationBellView;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel = new MainViewModel();
    private Menu toolbarMenu;
    private Fragment homeFragment, liveChatFragment;
    private SharedPreferences sharedPreferences;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        homeFragment = new HomeFragment();
        liveChatFragment = new LiveChatFragment();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

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

    private void setupNotificationBell(){
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf));
        VFSettings settings = new VFSettings(colors);

        VFNotificationBellView bellView;

        if(sharedPreferences.getBoolean(SettingKeys.showNotificationBellTopBar, false)){
            if(toolbarMenu == null){
                return;
            }

            bellView = (VFNotificationBellView) getLayoutInflater().inflate(R.layout.cview_bell, null);
            findViewById(R.id.home_bell).setVisibility(View.GONE);
            toolbarMenu.findItem(R.id.menu_main_auth).setActionView(bellView);
        } else {
            bellView = findViewById(R.id.home_bell);
            bellView.setVisibility(View.VISIBLE);

            if(toolbarMenu != null) {
                toolbarMenu.findItem(R.id.menu_main_auth).setActionView(null);
            }
        }

        bellView.applySettings(settings);
        bellView.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.light : VFTheme.dark);
        bellView.setBellClickedInterface(new NotificationBellClickedInterface() {
            @Override
            public void bellPressed(UUID userUUID) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra(IntentKeys.INTENT_USER_UUID, userUUID.toString());
                intent.putExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE, "feed");
                startActivity(intent);
            }
        });
        bellView.setActionCallback(new VFActionsInterface() {
            @Override
            public void onNewAction(VFActionType actionType, VFActionData action) {
                if(actionType == VFActionType.authPressed){
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }
            }
        });
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
        setupNotificationBell();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_toolbar, menu);
        toolbarMenu = menu;
        getAuthState();
        setupNotificationBell();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.menu_main_auth){
            viewModel.getAuthState(new VFAuthService.UserLoginStatusCallback() {
                @Override
                public void onSuccess(boolean userIsLoggedIn, String userUUID) {
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
        if(sharedPreferences.getBoolean(SettingKeys.showNotificationBellTopBar, false)) {
            return;
        }
        viewModel.getAuthState(new VFAuthService.UserLoginStatusCallback() {
            @Override
            public void onSuccess(boolean userIsLoggedIn, String userUUID) {
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