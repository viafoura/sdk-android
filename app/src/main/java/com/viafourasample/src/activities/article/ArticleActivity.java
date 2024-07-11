package com.viafourasample.src.activities.article;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.work.impl.model.Preference;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.viafourasample.src.activities.commentsContainer.CommentsContainerActivity;
import com.viafourasample.src.activities.login.LoginActivity;
import com.viafourasample.src.activities.newcomment.NewCommentActivity;
import com.viafourasample.src.activities.profile.ProfileActivity;
import com.viafourasample.src.managers.ColorManager;
import com.viafourasample.src.model.IntentKeys;
import com.viafourasample.src.model.SettingKeys;
import com.viafourasample.src.model.Story;
import com.viafourasdk.src.fragments.base.VFFragment;
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragment;
import com.viafourasdk.src.fragments.trending.VFVerticalTrendingFragment;
import com.viafourasdk.src.interfaces.VFActionsInterface;
import com.viafourasdk.src.interfaces.VFAdInterface;
import com.viafourasdk.src.interfaces.VFContentScrollPositionInterface;
import com.viafourasdk.src.interfaces.VFCustomUIInterface;
import com.viafourasdk.src.interfaces.VFLayoutInterface;
import com.viafourasdk.src.interfaces.VFLoginInterface;
import com.viafourasdk.src.model.local.VFActionData;
import com.viafourasdk.src.model.local.VFActionType;
import com.viafourasdk.src.model.local.VFArticleMetadata;
import com.viafourasdk.src.model.local.VFColors;
import com.viafourasdk.src.model.local.VFCommentsContainerType;
import com.viafourasdk.src.model.local.VFCustomViewType;
import com.viafourasdk.src.model.local.VFDefaultColors;
import com.viafourasdk.src.model.local.VFFonts;
import com.viafourasdk.src.model.local.VFNotificationPresentationAction;
import com.viafourasdk.src.model.local.VFSettings;
import com.viafourasdk.src.model.local.VFSortType;
import com.viafourasdk.src.model.local.VFTheme;
import com.viafourasdk.src.model.local.VFTrendingSortType;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

public class ArticleActivity extends AppCompatActivity implements VFLoginInterface, VFCustomUIInterface, VFActionsInterface, VFAdInterface, VFLayoutInterface, VFContentScrollPositionInterface {

    private ArticleViewModel articleViewModel;
    private ScrollView scrollView;
    private VFSettings vfSettings;
    private SharedPreferences preferences;

    public static final String TAG_COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT";

    interface WebViewDelegate {
        void triggerEngagementStarter();
    }

    public class WebViewInterface {
        private WebViewDelegate webViewDelegate;

        public WebViewInterface(WebViewDelegate webViewDelegate){
            this.webViewDelegate = webViewDelegate;
        }
        @JavascriptInterface
        public void triggerEngagementStarter(){
           webViewDelegate.triggerEngagementStarter();
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        articleViewModel = new ArticleViewModel(getIntent().getStringExtra(IntentKeys.INTENT_CONTAINER_ID));

        VFColors colors = new VFColors(ContextCompat.getColor(getApplicationContext(), R.color.colorVfDark), ContextCompat.getColor(getApplicationContext(), R.color.colorVf));
        vfSettings = new VFSettings(colors);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(articleViewModel.getStory().getTitle());

        ((ProgressBar) findViewById(R.id.article_loading)).getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN);

        scrollView = findViewById(R.id.article_scroll);

        if(ColorManager.isDarkMode(getApplicationContext())){
            scrollView.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
        }

        WebView webView = findViewById(R.id.article_webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        WebViewInterface webViewInterface = new WebViewInterface(new WebViewDelegate() {
            @Override
            public void triggerEngagementStarter() {
                float yPosition = (findViewById(R.id.article_comments_container).getY());
                scrollView.smoothScrollTo(0, (int) yPosition);
            }
        });

        webView.addJavascriptInterface(webViewInterface, "NativeAndroid");
        webView.loadUrl(articleViewModel.getStory().getLink());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                if(request.getUrl().toString().equals(articleViewModel.getStory().getLink())){
                    return false;
                }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

            }

            public void onPageFinished(WebView view, String url) {
                if(ColorManager.isDarkMode(getApplicationContext())){
                    view.evaluateJavascript("document.documentElement.classList.add('dark');", null);
                }

                view.evaluateJavascript("setTimeout(function() { document.querySelector('.vf-conversation-starter_link').onclick = function() {  NativeAndroid.triggerEngagementStarter(); }; document.querySelector('.vf-editors-pick_container-actions').onclick = function() {  NativeAndroid.triggerEngagementStarter(); }; }, 5000);", null);

                findViewById(R.id.article_loading).setVisibility(View.GONE);
                if(preferences.getBoolean(SettingKeys.commentsContainerFullscreen, false)) {
                    findViewById(R.id.article_comments_fullscreen).setVisibility(View.VISIBLE);
                } else {
                    addCommentsFragment();
                }

            }
        });

        findViewById(R.id.article_comments_fullscreen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CommentsContainerActivity.class);
                intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, articleViewModel.getStory().getContainerId());
                startActivity(intent);
            }
        });
    }

    private void addCommentsFragment() {
        if(getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_FRAGMENT) != null){
            return;
        }

        VFArticleMetadata articleMetadata = new VFArticleMetadata(articleViewModel.getStory().getLink(), articleViewModel.getStory().getTitle(), articleViewModel.getStory().getDescription(), articleViewModel.getStory().getPictureUrl());
        VFPreviewCommentsFragment previewCommentsFragment = VFPreviewCommentsFragment.newInstance(articleViewModel.getStory().getContainerId(), articleMetadata, this, vfSettings, 10, VFSortType.newest, 0, 0, null, articleViewModel.getStory().getStoryType() == Story.StoryType.comments ? VFCommentsContainerType.conversations : VFCommentsContainerType.reviews);
        previewCommentsFragment.setTheme(ColorManager.isDarkMode(getApplicationContext()) ? VFTheme.dark : VFTheme.light);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.article_comments_container, previewCommentsFragment, TAG_COMMENTS_FRAGMENT);
        ft.commitAllowingStateLoss();

        if(getIntent().getStringExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID) != null){
            previewCommentsFragment.setFocusContent(UUID.fromString(getIntent().getStringExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID)));
        }

        previewCommentsFragment.setScrollPositionCallback(this);
        previewCommentsFragment.setLayoutCallback(this);
        previewCommentsFragment.setActionCallback(this);
        previewCommentsFragment.setAdInterface(this);
        previewCommentsFragment.setCustomUICallback(this);
        previewCommentsFragment.setAuthorIds(Collections.singletonList("3147700024522"));
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
            } else if(action.getNotificationPresentationAction().notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.content){
                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
                intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, articleViewModel.getStory().getContainerId());
                intent.putExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID, action.getNotificationPresentationAction().contentUUID.toString());
                startActivity(intent);
            }
        } else if(actionType == VFActionType.trendingArticlePressed){
            Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, action.getTrendingPressedAction().containerId);
            startActivity(intent);
        }
    }

    @Override
    public void customizeView(VFTheme theme, VFCustomViewType customViewType, View view) {
        switch (customViewType){
            case previewBackgroundView:
                if(theme == VFTheme.dark){
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
                }
                break;
            case trendingVerticalBackground:
                if(theme == VFTheme.dark){
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
                }
                break;
            case trendingCarouselBackground:
                if(theme == VFTheme.dark){
                    view.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.colorBackgroundArticle));
                }
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
            TextView adText = adLayout.findViewById(R.id.row_ad_title);

            adText.setTextColor(ColorManager.isDarkMode(getApplicationContext()) ? Color.WHITE : Color.BLACK);

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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        VFPreviewCommentsFragment commentsFragment = (VFPreviewCommentsFragment) getSupportFragmentManager().findFragmentByTag(TAG_COMMENTS_FRAGMENT);
        if(commentsFragment != null){
            commentsFragment.setScrollPositionCallback(this);
            commentsFragment.setLayoutCallback(this);
            commentsFragment.setActionCallback(this);
            commentsFragment.setAdInterface(this);
            commentsFragment.setCustomUICallback(this);
        }
    }

    @Override
    public void scrollToPosition(int position) {
        int yPosition = (int) (findViewById(R.id.article_comments_container).getY() + position);
        scrollView.smoothScrollTo(0, yPosition);
    }

    @Override
    public void containerHeightUpdated(VFFragment fragment, String containerId, int height) {

    }
}