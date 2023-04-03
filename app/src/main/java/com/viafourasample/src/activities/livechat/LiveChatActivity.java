package com.viafourasample.src.activities.livechat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.fragments.livechat.VFLiveChatFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;

import java.net.MalformedURLException;
import java.net.URL;

public class LiveChatActivity extends AppCompatActivity implements VFLoginInterface, VFActionsInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        setTitle(getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE));

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf), Color.WHITE);
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata metadata = null;
        try {
            metadata = new VFArticleMetadata(new URL("https://viafoura-mobile-demo.vercel.app"), getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE), "", new URL("https://viafoura-mobile-demo.vercel.app"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        VFLiveChatFragment liveChatFragment = VFLiveChatFragment.newInstance(getApplication(), "Container", metadata, this, vfSettings);
        liveChatFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
        liveChatFragment.setActionCallback(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.live_chat_container, liveChatFragment);
        ft.commit();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startLogin() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
    }

    @Override
    public void onNewAction(VFActionType actionType, VFActionData action) {
        if(actionType == VFActionType.openProfilePressed){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getOpenProfileAction().userUUID.toString());
            startActivity(intent);
        }
    }
}