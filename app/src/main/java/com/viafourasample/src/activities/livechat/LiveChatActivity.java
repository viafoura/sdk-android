package com.viafourasample.src.activities.livechat;

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
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasdk.src.fragments.livechat.VFLiveChatFragment;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;

import java.net.MalformedURLException;
import java.net.URL;

public class LiveChatActivity extends AppCompatActivity implements VFLoginInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf), Color.WHITE);
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata metadata = null;
        try {
            metadata = new VFArticleMetadata(new URL("https://viafoura-mobile-demo.vercel.app"), "", "", new URL("https://viafoura-mobile-demo.vercel.app"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        VFLiveChatFragment liveChatFragment = VFLiveChatFragment.newInstance(getApplication(), "Container", metadata, this, vfSettings);
        liveChatFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
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

    }
}