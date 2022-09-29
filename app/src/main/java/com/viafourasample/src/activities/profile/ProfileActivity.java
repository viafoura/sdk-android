package com.viafourasample.src.activities.profile;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.viafoura.viafourasdk.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasdk.src.fragments.profile.ProfileFragment;
import com.viafourasdk.src.fragments.profile.ProfileViewModel;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFOpenProfileAction;
import com.viafourasdk.src.model.local.VFProfilePresentationType;
import com.viafourasdk.src.model.local.VFSettings;

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
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight), Color.WHITE);
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
        ProfileFragment profileFragment = ProfileFragment.newInstance(getApplication(), UUID.fromString(getIntent().getStringExtra(IntentKeys.INTENT_USER_UUID)), presentationType, this, vfSettings);
        profileFragment.setActionCallback(this);
        profileFragment.setCustomUICallback(this);
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
    }

    @Override
    public void customizeView(VFCustomViewType viewType, View view) {

    }
}