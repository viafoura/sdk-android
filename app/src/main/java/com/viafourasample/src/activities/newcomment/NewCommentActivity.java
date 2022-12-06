package com.viafourasample.src.activities.newcomment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFNewCommentAction;
import com.viafourasdk.src.model.local.VFSettings;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class NewCommentActivity extends AppCompatActivity implements VFActionsInterface, VFCustomUIInterface, VFLoginInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Nuevo comentario");

        try {
            addNewCommentsFragment();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void addNewCommentsFragment() throws MalformedURLException {
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryLight), Color.WHITE);
        VFSettings vfSettings = new VFSettings(colors);
        URL storyUrl = new URL(getIntent().getStringExtra(IntentKeys.INTENT_STORY_LINK));
        URL pictureUrl = new URL(getIntent().getStringExtra(IntentKeys.INTENT_STORY_PICTUREURL));
        VFArticleMetadata articleMetadata = new VFArticleMetadata(storyUrl, getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE), getIntent().getStringExtra(IntentKeys.INTENT_STORY_DESC), pictureUrl);

        VFNewCommentAction newCommentAction = null;

        String newCommentActionType = getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION);

        if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.create.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.create);
        } else if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.reply.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.reply);
        } else if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.edit.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.edit);
        }

        if(getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT) != null){
            newCommentAction.content = UUID.fromString(getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT));
        }

        VFNewCommentFragment newCommentFragment = VFNewCommentFragment.newInstance(getApplication(), newCommentAction, getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID), articleMetadata, this, vfSettings);
        newCommentFragment.setActionCallback(this);
        newCommentFragment.setCustomUICallback(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.new_comment_container, newCommentFragment);
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
        if(actionType == VFActionType.closeNewCommentPressed){
            onBackPressed();
        }
    }

    @Override
    public void customizeView(VFCustomViewType viewType, View view) {

    }
}