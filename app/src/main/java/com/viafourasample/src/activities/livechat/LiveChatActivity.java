package com.viafourasample.src.activities.livechat;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasdk.src.fragments.livechat.VFLiveChatFragment;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;

import java.net.MalformedURLException;
import java.net.URL;

public class LiveChatActivity extends AppCompatActivity implements VFLoginInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight), Color.WHITE);
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata metadata = null;
        try {
            metadata = new VFArticleMetadata(new URL("https://test.com"), "", "", new URL("https://test.com"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        VFLiveChatFragment liveChatFragment = VFLiveChatFragment.newInstance(getApplication(), "Container", metadata, this, vfSettings);
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