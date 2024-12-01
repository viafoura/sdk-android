package com.viafourasample.src.activities.commentsContainer;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;

import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.newcomment.NewCommentActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragment;
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragmentBuilder;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFTheme;

public class CommentsContainerActivity extends AppCompatActivity implements VFActionsInterface, VFCustomUIInterface {
    private VFSettings vfSettings;
    private CommentsContainerViewModel commentsContainerViewModel;

    public static final String TAG_COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_container);

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf));
        vfSettings = new VFSettings(colors);

        commentsContainerViewModel = new CommentsContainerViewModel(getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Comments");

        if(ColorManager.isDarkMode(getApplicationContext())){
            findViewById(R.id.comments_container_scroll).setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        }

        addCommentsFragment();
    }

    private void addCommentsFragment() {
        VFArticleMetadata articleMetadata = new VFArticleMetadata(commentsContainerViewModel.getStory().getLink(), commentsContainerViewModel.getStory().getTitle(), commentsContainerViewModel.getStory().getDescription(), commentsContainerViewModel.getStory().getPictureUrl());
        VFPreviewCommentsFragment previewCommentsFragment = new VFPreviewCommentsFragmentBuilder(commentsContainerViewModel.getStory().getContainerId(), articleMetadata, vfSettings).build();
        previewCommentsFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.comments_container, previewCommentsFragment, TAG_COMMENTS_FRAGMENT);
        ft.commitAllowingStateLoss();

        previewCommentsFragment.setCustomUICallback(this);
        previewCommentsFragment.setActionCallback(this);
    }

    @Override
    public void onNewAction(VFActionType actionType, VFActionData action) {
        if(actionType == VFActionType.writeNewCommentPressed){
            Intent intent = new Intent(getApplicationContext(), NewCommentActivity.class);
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, commentsContainerViewModel.getStory().getContainerId());
            intent.putExtra(IntentKeys.INTENT_STORY_LINK, commentsContainerViewModel.getStory().getLink());
            intent.putExtra(IntentKeys.INTENT_STORY_TITLE, commentsContainerViewModel.getStory().getTitle());
            intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION, action.getNewCommentAction().type.toString());
            intent.putExtra(IntentKeys.INTENT_CONTAINER_TYPE, commentsContainerViewModel.getStory().getStoryType().toString());
            if(action.getNewCommentAction().content != null){
                intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT, action.getNewCommentAction().content.toString());
            }
            intent.putExtra(IntentKeys.INTENT_STORY_DESC, commentsContainerViewModel.getStory().getDescription());
            intent.putExtra(IntentKeys.INTENT_STORY_PICTUREURL, commentsContainerViewModel.getStory().getPictureUrl());
            startActivity(intent);
        } else if(actionType == VFActionType.openProfilePressed){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getOpenProfileAction().userUUID.toString());
            if(action.getOpenProfileAction().presentationType != null){
                intent.putExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE, action.getOpenProfileAction().presentationType.toString());
            }
            startActivity(intent);
        } else if(actionType == VFActionType.authPressed){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        VFPreviewCommentsFragment commentsFragment = (VFPreviewCommentsFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_FRAGMENT);
        if(commentsFragment != null){
            commentsFragment.setActionCallback(this);
            commentsFragment.setCustomUICallback(this);
        }
    }

    @Override
    public void customizeView(VFTheme theme, VFCustomViewType customViewType, View view) {
        switch (customViewType){
            case previewBackgroundView:
                if(theme == VFTheme.dark){
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
                }
                return;
        }
    }
}