package com.viafourasample.src.activities.livechat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.viafoura.sampleapp.R;
import com.viafourasdk.src.fragments.livechat.LiveChatFragment;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFSettings;

public class LiveChatActivity extends AppCompatActivity implements VFLoginInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_chat);

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight), Color.WHITE);
        VFSettings vfSettings = new VFSettings(colors);
        LiveChatFragment liveChatFragment = LiveChatFragment.newInstance("Container", this, vfSettings);
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