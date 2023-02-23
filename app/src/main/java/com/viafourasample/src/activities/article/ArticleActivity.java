package com.viafourasample.src.activities.article;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.viafoura.sampleapp.R;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.newcomment.NewCommentActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.Story;
import com.viafourasdk.src.fragments.base.VFFragment;
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragment;
import com.viafourasdk.src.fragments.trending.VFVerticalTrendingFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFAdInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.interfaces.VFLayoutInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFDefaultColors;
import com.viafourasdk.src.model.local.VFFonts;
import com.viafourasdk.src.model.local.VFNotificationPresentationAction;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFSortType;
import com.viafourasdk.src.model.local.VFTheme;
import com.viafourasdk.src.model.local.VFTrendingSortType;
import com.viafourasdk.src.model.local.VFTrendingViewType;
import com.viafourasdk.src.view.VFTextView;

import java.net.MalformedURLException;
import java.net.URL;

public class ArticleActivity extends AppCompatActivity implements VFLoginInterface, VFCustomUIInterface, VFActionsInterface, VFAdInterface, VFLayoutInterface {

    private ArticleViewModel articleViewModel;
    private ScrollView scrollView;
    private VFSettings vfSettings;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        articleViewModel = new ArticleViewModel(
                new Story(getIntent().getStringExtra(IntentKeys.INTENT_STORY_PICTUREURL),
                        getIntent().getStringExtra(IntentKeys.INTENT_STORY_TITLE),
                        getIntent().getStringExtra(IntentKeys.INTENT_STORY_DESC),
                        getIntent().getStringExtra(IntentKeys.INTENT_STORY_AUTHOR),
                        getIntent().getStringExtra(IntentKeys.INTENT_STORY_CATEGORY),
                        getIntent().getStringExtra(IntentKeys.INTENT_STORY_LINK),
                        getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID))
        );

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf), Color.WHITE);
        vfSettings = new VFSettings(colors);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(articleViewModel.getStory().getTitle());

        ((ProgressBar) findViewById(R.id.article_loading)).getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);

        scrollView = findViewById(R.id.article_scroll);

        WebView webView = findViewById(R.id.article_webview);
        webView.loadUrl(articleViewModel.getStory().getLink());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            public void onPageFinished(WebView view, String url) {
                findViewById(R.id.article_loading).setVisibility(View.GONE);
                try {
                    addCommentsFragment();
                    addTrendingFragment();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addTrendingFragment(){
        VFVerticalTrendingFragment trendingFragment = VFVerticalTrendingFragment.newInstance(getApplication(), "", "Trending content", 10, 10, 10, VFTrendingSortType.comments, VFTrendingViewType.full, vfSettings);
        trendingFragment.setAdInterface(this);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.article_trending_container, trendingFragment);
        ft.commitAllowingStateLoss();
    }

    private void addCommentsFragment() throws MalformedURLException {
        VFArticleMetadata articleMetadata = new VFArticleMetadata(new URL(articleViewModel.getStory().getLink()), articleViewModel.getStory().getTitle(), articleViewModel.getStory().getDescription(), new URL(articleViewModel.getStory().getPictureUrl()));
        VFPreviewCommentsFragment previewCommentsFragment = VFPreviewCommentsFragment.newInstance(getApplication(), articleViewModel.getStory().getContainerId(), articleMetadata, this, vfSettings, 10, VFSortType.mostLiked);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.article_comments_container, previewCommentsFragment);
        ft.commitAllowingStateLoss();

        previewCommentsFragment.setLayoutCallback(this);
        previewCommentsFragment.setActionCallback(this);
        previewCommentsFragment.setAdInterface(this);
        previewCommentsFragment.setCustomUICallback(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void startLogin() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onNewAction(VFActionType actionType, VFActionData action) {
        if(actionType == VFActionType.writeNewCommentPressed){
            Intent intent = new Intent(getApplicationContext(), NewCommentActivity.class);
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, articleViewModel.getStory().getContainerId());
            intent.putExtra(IntentKeys.INTENT_STORY_LINK, articleViewModel.getStory().getLink());
            intent.putExtra(IntentKeys.INTENT_STORY_TITLE, articleViewModel.getStory().getTitle());
            intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION, action.getNewCommentAction().type.toString());
            if(action.getNewCommentAction().content != null){
                intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT, action.getNewCommentAction().content.toString());
            }
            intent.putExtra(IntentKeys.INTENT_STORY_DESC, articleViewModel.getStory().getDescription());
            intent.putExtra(IntentKeys.INTENT_STORY_PICTUREURL, articleViewModel.getStory().getPictureUrl());
            startActivity(intent);
        } else if(actionType == VFActionType.openProfilePressed){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getOpenProfileAction().userUUID.toString());
            if(action.getOpenProfileAction().presentationType != null){
                intent.putExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE, action.getOpenProfileAction().presentationType.toString());
            }
            startActivity(intent);
        } else if(actionType == VFActionType.notificationPressed){
            if(action.getNotificationPresentationAction().notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.profile){
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                intent.putExtra(IntentKeys.INTENT_USER_UUID, action.getNotificationPresentationAction().userUUID.toString());
                startActivity(intent);
            }
        }
    }

    @Override
    public void customizeView(VFTheme theme, VFCustomViewType customViewType, View view) {
        switch (customViewType){
            case commentCellCommentText:
                break;
        }
    }

    @Override
    public int getFirstAdPosition(VFFragment fragment) {
        return 5;
    }

    @Override
    public int getAdInterval(VFFragment fragment) {
        return 3;
    }

    @Override
    public ViewGroup generateAd(VFFragment fragment, int adPosition) {
        if(adPosition % 2 == 0){
            RelativeLayout adContainer = new RelativeLayout(this);
            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.BANNER);
            adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");

            AdRequest adRequest = new AdRequest.Builder()
                    .build();
            adView.loadAd(adRequest);

            adView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300));
            adContainer.addView(adView);
            return adContainer;
        } else {
            String service = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(service);
            RelativeLayout adLayout = (RelativeLayout) li.inflate(R.layout.row_ad, null);
            ImageView adImage = adLayout.findViewById(R.id.row_ad_image);

            RequestOptions requestOptions = new RequestOptions();
            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(4));

            Glide.with(getApplicationContext())
                    .asBitmap()
                    .load("https://images.outbrainimg.com/transform/v3/eyJpdSI6IjYwNjA2OWRiMjFiZTc0ODAyOWEzZDAwYTczM2E2YjkxNzM2ZWZmODczYWQ5NjcyMzQzN2YxOGU2YTJhYmQ3NGYiLCJ3IjozNzUsImgiOjEyNSwiZCI6MS41LCJjcyI6MCwiZiI6NH0.webp")
                    .apply(requestOptions)
                    .into(adImage);
            return adLayout;
        }
    }

    @Override
    public void containerHeightUpdated(VFFragment fragment, int height) {

    }
}