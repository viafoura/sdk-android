package com.viafourasample.src.activities.article

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.commentsContainer.CommentsContainerActivity
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.newcomment.NewCommentActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.model.SettingKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.base.VFFragment
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragment
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFAdInterface
import com.viafourasdk.src.interfaces.VFContentScrollPositionInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.interfaces.VFLayoutInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFNotificationPresentationAction
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFSortType
import com.viafourasdk.src.model.local.VFTheme
import com.viafourasdk.src.utils.VFInsetsUtils
import java.util.UUID

class ArticleActivity : AppCompatActivity(), VFCustomUIInterface, VFActionsInterface, VFAdInterface,
    VFLayoutInterface, VFContentScrollPositionInterface {

    private lateinit var articleViewModel: ArticleViewModel
    private lateinit var scrollView: ScrollView
    private lateinit var vfSettings: VFSettings
    private lateinit var preferences: SharedPreferences

    fun interface WebViewDelegate {
        fun triggerEngagementStarter()
    }

    inner class WebViewInterface(private val webViewDelegate: WebViewDelegate) {
        @JavascriptInterface
        fun triggerEngagementStarter() {
            webViewDelegate.triggerEngagementStarter()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_article)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsToScrollable(findViewById<View>(R.id.article_scroll))

        preferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        articleViewModel = ArticleViewModel(intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID)!!)

        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorVfDark),
            ContextCompat.getColor(applicationContext, R.color.colorVf)
        )
        vfSettings = VFSettings(colors)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = articleViewModel.story.title

        findViewById<ProgressBar>(R.id.article_loading).indeterminateDrawable.setColorFilter(
            resources.getColor(R.color.colorPrimary),
            PorterDuff.Mode.SRC_IN
        )

        scrollView = findViewById(R.id.article_scroll)

        if (ColorManager.isDarkMode(applicationContext)) {
            scrollView.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        val webView = findViewById<WebView>(R.id.article_webview)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        val webViewInterface = WebViewInterface(WebViewDelegate {
            val yPosition = findViewById<View>(R.id.article_comments_container).y
            scrollView.smoothScrollTo(0, yPosition.toInt())
        })

        webView.addJavascriptInterface(webViewInterface, "NativeAndroid")
        webView.loadUrl(articleViewModel.story.link)
        webView.webViewClient = object : WebViewClient() {

            override fun shouldOverrideUrlLoading(
                view: WebView,
                request: WebResourceRequest
            ): Boolean = request.url.toString() != articleViewModel.story.link

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest,
                error: WebResourceError
            ) {
            }

            override fun onPageFinished(view: WebView, url: String) {
                if (ColorManager.isDarkMode(applicationContext)) {
                    view.evaluateJavascript("document.documentElement.classList.add('dark');", null)
                }

                view.evaluateJavascript(
                    "setTimeout(function() { document.querySelector('.vf-conversation-starter_link').onclick = function() {  NativeAndroid.triggerEngagementStarter(); }; document.querySelector('.vf-editors-pick_container-actions').onclick = function() {  NativeAndroid.triggerEngagementStarter(); }; }, 5000);",
                    null
                )

                findViewById<View>(R.id.article_loading).visibility = View.GONE
                if (preferences.getBoolean(SettingKeys.commentsContainerFullscreen, false)) {
                    findViewById<View>(R.id.article_comments_fullscreen).visibility = View.VISIBLE
                } else {
                    addCommentsFragment()
                }
            }
        }

        findViewById<View>(R.id.article_comments_fullscreen).setOnClickListener {
            val intent = Intent(applicationContext, CommentsContainerActivity::class.java)
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, articleViewModel.story.containerId)
            startActivity(intent)
        }
    }

    private fun addCommentsFragment() {
        if (supportFragmentManager.findFragmentByTag(TAG_COMMENTS_FRAGMENT) != null) {
            return
        }

        val story = articleViewModel.story
        val articleMetadata =
            VFArticleMetadata(story.link, story.title, story.description, story.pictureUrl)
        val previewCommentsFragment =
            VFPreviewCommentsFragmentBuilder(story.containerId, articleMetadata, vfSettings)
                .paginationSize(10)
                .sortType(VFSortType.newest)
                .build()
        previewCommentsFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.article_comments_container, previewCommentsFragment, TAG_COMMENTS_FRAGMENT)
        ft.commitAllowingStateLoss()

        intent.getStringExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID)?.let {
            previewCommentsFragment.setFocusContent(UUID.fromString(it))
        }

        previewCommentsFragment.setScrollPositionCallback(this)
        previewCommentsFragment.setLayoutCallback(this)
        previewCommentsFragment.setActionCallback(this)
        previewCommentsFragment.setAdInterface(this)
        previewCommentsFragment.setCustomUICallback(this)
        previewCommentsFragment.setAuthorIds(listOf("3147700024522"))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        val story = articleViewModel.story
        if (actionType == VFActionType.writeNewCommentPressed) {
            val newCommentAction = action.newCommentAction!!
            val intent = Intent(applicationContext, NewCommentActivity::class.java)
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.containerId)
            intent.putExtra(IntentKeys.INTENT_STORY_LINK, story.link)
            intent.putExtra(IntentKeys.INTENT_STORY_TITLE, story.title)
            intent.putExtra(IntentKeys.INTENT_CONTAINER_TYPE, story.storyType.toString())
            intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION, newCommentAction.type.toString())
            newCommentAction.content?.let {
                intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT, it.toString())
            }
            intent.putExtra(IntentKeys.INTENT_STORY_DESC, story.description)
            intent.putExtra(IntentKeys.INTENT_STORY_PICTUREURL, story.pictureUrl)
            startActivity(intent)
        } else if (actionType == VFActionType.openProfilePressed) {
            val openProfileAction = action.openProfileAction!!
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.putExtra(IntentKeys.INTENT_USER_UUID, openProfileAction.userUUID.toString())
            openProfileAction.presentationType?.let {
                intent.putExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE, it.toString())
            }
            startActivity(intent)
        } else if (actionType == VFActionType.notificationPressed) {
            val notification = action.notificationPresentationAction!!
            if (notification.notificationPresentationType ==
                VFNotificationPresentationAction.VFNotificationPresentationType.profile
            ) {
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                intent.putExtra(IntentKeys.INTENT_USER_UUID, notification.userUUID.toString())
                startActivity(intent)
            } else if (notification.notificationPresentationType ==
                VFNotificationPresentationAction.VFNotificationPresentationType.content
            ) {
                val intent = Intent(applicationContext, ArticleActivity::class.java)
                intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.containerId)
                intent.putExtra(
                    IntentKeys.INTENT_FOCUS_CONTENT_UUID,
                    notification.contentUUID.toString()
                )
                startActivity(intent)
            }
        } else if (actionType == VFActionType.trendingArticlePressed) {
            val intent = Intent(applicationContext, ArticleActivity::class.java)
            intent.putExtra(
                IntentKeys.INTENT_CONTAINER_ID,
                action.trendingPressedAction!!.containerId
            )
            startActivity(intent)
        } else if (actionType == VFActionType.authPressed) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: View) {
        when (customViewType) {
            VFCustomViewType.previewBackgroundView,
            VFCustomViewType.trendingVerticalBackground,
            VFCustomViewType.trendingCarouselBackground -> {
                if (theme == VFTheme.dark) {
                    view.setBackgroundColor(
                        ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
                    )
                }
            }

            else -> {}
        }
    }

    override fun getFirstAdPosition(fragment: VFFragment): Int = 5

    override fun getAdInterval(fragment: VFFragment): Int = 3

    override fun generateAd(fragment: VFFragment, adPosition: Int): ViewGroup {
        if (adPosition % 2 == 0) {
            val adContainer = RelativeLayout(this)
            val adView = AdView(applicationContext)
            adView.setAdSize(AdSize.BANNER)
            adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"

            adView.loadAd(AdRequest.Builder().build())

            adView.layoutParams =
                ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 300)
            adContainer.addView(adView)
            return adContainer
        } else {
            val service = Context.LAYOUT_INFLATER_SERVICE
            val li = applicationContext.getSystemService(service) as LayoutInflater
            val adLayout = li.inflate(R.layout.row_ad, null) as RelativeLayout
            val adImage = adLayout.findViewById<ImageView>(R.id.row_ad_image)
            val adText = adLayout.findViewById<TextView>(R.id.row_ad_title)

            adText.setTextColor(
                if (ColorManager.isDarkMode(applicationContext)) Color.WHITE else Color.BLACK
            )

            val requestOptions = RequestOptions().transforms(CenterCrop(), RoundedCorners(4))

            Glide.with(applicationContext)
                .asBitmap()
                .load("https://images.outbrainimg.com/transform/v3/eyJpdSI6IjYwNjA2OWRiMjFiZTc0ODAyOWEzZDAwYTczM2E2YjkxNzM2ZWZmODczYWQ5NjcyMzQzN2YxOGU2YTJhYmQ3NGYiLCJ3IjozNzUsImgiOjEyNSwiZCI6MS41LCJjcyI6MCwiZiI6NH0.webp")
                .apply(requestOptions)
                .into(adImage)

            return adLayout
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val commentsFragment =
            supportFragmentManager.findFragmentByTag(TAG_COMMENTS_FRAGMENT) as? VFPreviewCommentsFragment
        commentsFragment?.let {
            it.setScrollPositionCallback(this)
            it.setLayoutCallback(this)
            it.setActionCallback(this)
            it.setAdInterface(this)
            it.setCustomUICallback(this)
        }
    }

    override fun scrollToPosition(position: Int) {
        val yPosition = (findViewById<View>(R.id.article_comments_container).y + position).toInt()
        scrollView.smoothScrollTo(0, yPosition)
    }

    override fun containerHeightUpdated(fragment: VFFragment, containerId: String, height: Int) {
    }

    companion object {
        const val TAG_COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT"
    }
}
