package com.viafourasample.src.activities.newcomment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragment;
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragmentBuilder;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCommentsContainerType;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFNewCommentAction;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;

import java.util.UUID;

public class NewCommentActivity extends AppCompatActivity implements VFActionsInterface, VFCustomUIInterface {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_comment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Nuevo comentario");

        addNewCommentsFragment();
    }

    private void addNewCommentsFragment() {
        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf));
        VFSettings vfSettings = new VFSettings(colors);
        VFArticleMetadata articleMetadata = new VFArticleMetadata(getIntent().getStringExtra(IntentKeys.INTENT_STORY_LINK), getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE), getIntent().getStringExtra(IntentKeys.INTENT_STORY_DESC), getIntent().getStringExtra(IntentKeys.INTENT_STORY_PICTUREURL));

        VFCommentsContainerType containerType = VFCommentsContainerType.conversations;
        VFNewCommentAction newCommentAction = null;

        String newCommentActionType = getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION);
        String containerTypeValue = getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_TYPE);

        if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.create.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.create);
        } else if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.reply.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.reply);
        } else if(newCommentActionType.equals(VFNewCommentAction.VFNewCommentActionType.edit.toString())){
            newCommentAction = new VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.edit);
        }

        if(containerTypeValue.equals(VFCommentsContainerType.conversations.toString())){
            containerType = VFCommentsContainerType.conversations;
        } else if(containerTypeValue.equals(VFCommentsContainerType.reviews.toString())) {
            containerType = VFCommentsContainerType.reviews;
        }

        if(getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT) != null){
            newCommentAction.content = UUID.fromString(getIntent().getStringExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT));
        }

        VFNewCommentFragment newCommentFragment = new VFNewCommentFragmentBuilder(newCommentAction, getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID), articleMetadata, vfSettings)
                .build();

        newCommentFragment.setActionCallback(this);
        newCommentFragment.setCustomUICallback(this);
        newCommentFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.new_comment_container, newCommentFragment);
        ft.commit();
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
        } else if(actionType == VFActionType.authPressed){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void customizeView(VFTheme theme, VFCustomViewType viewType, View view) {

    }
}