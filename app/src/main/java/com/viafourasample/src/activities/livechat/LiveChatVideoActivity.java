package com.viafourasample.src.activities.livechat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.VideoView;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
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

public class LiveChatVideoActivity extends AppCompatActivity implements VFLoginInterface, VFActionsInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_live_chat_video);

        getSupportActionBar().hide();

        setTitle(getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE));

        setupClose();
        setupVideo();
        setupGradient();
        setupLiveChatWidget();
    }

    private void setupClose(){
        findViewById(R.id.live_chat_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void setupVideo(){
        VideoView videoView = findViewById(R.id.live_chat_video);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.livechat_video;
        videoView.setVideoURI(Uri.parse(path));
        videoView.requestFocus();
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
    }

    private void setupLiveChatWidget(){
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf), Color.TRANSPARENT);
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata metadata = new VFArticleMetadata("https://viafoura-mobile-demo.vercel.app", getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE), "", "https://viafoura-mobile-demo.vercel.app");
        VFLiveChatFragment liveChatFragment = VFLiveChatFragment.newInstance(getApplication(), getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID), metadata, this, vfSettings);
        liveChatFragment.setTheme(VFTheme.dark);
        liveChatFragment.setActionCallback(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.live_chat_container, liveChatFragment);
        ft.commit();
    }

    private void setupGradient(){
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[] {
                        Color.TRANSPARENT, Color.BLACK, Color.BLACK
                });

        findViewById(R.id.live_chat_container).setBackground(gd);
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