package com.viafourasample.src.activities.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.impl.model.Preference;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.article.ArticleActivity;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.fragments.profile.VFProfileFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFNotificationPresentationAction;
import com.viafourasdk.src.model.local.VFProfilePresentationType;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;

import java.net.MalformedURLException;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity implements VFActionsInterface, VFCustomUIInterface, VFLoginInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.profile));

        try {
            addProfileFragment();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addProfileFragment() throws MalformedURLException {
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf));
        VFSettings vfSettings = new VFSettings(colors);
        VFProfilePresentationType presentationType = VFProfilePresentationType.profile;
        if(getIntent().getStringExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE) != null){
            String presentationTypeString = getIntent().getStringExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE);
            if(presentationTypeString.equals(VFProfilePresentationType.profile.toString())){
                presentationType = VFProfilePresentationType.profile;
            } else if(presentationTypeString.equals(VFProfilePresentationType.feed.toString())){
                presentationType = VFProfilePresentationType.feed;
            }
        }
        VFProfileFragment profileFragment = VFProfileFragment.newInstance(getApplication(), UUID.fromString(getIntent().getStringExtra(IntentKeys.INTENT_USER_UUID)), presentationType, this, vfSettings);
        profileFragment.setActionCallback(this);
        profileFragment.setCustomUICallback(this);
        profileFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.profile_container, profileFragment);
        ft.commit();
    }

    @Override
    public void startLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewAction(VFActionType actionType, VFActionData action) {
        if(actionType == VFActionType.closeProfilePressed){
            onBackPressed();
        }
        else if(actionType == VFActionType.notificationPressed){
            if(action.getNotificationPresentationAction().notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.profile){
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getNotificationPresentationAction().userUUID.toString());
                startActivity(intent);
            } else if(action.getNotificationPresentationAction().notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.content){
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, action.getNotificationPresentationAction().containerId.toString());
                intent.putExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID, action.getNotificationPresentationAction().contentUUID.toString());
                startActivity(intent);
            }
        }
    }

    @Override
    public void customizeView(VFTheme theme, VFCustomViewType customViewType, View view) {

    }
}