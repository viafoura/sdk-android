package com.viafourasample.src.activities.article

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
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
import com.viafourasdk.src.fragments.conversationstarter.VFConversationStarterFragment
import com.viafourasdk.src.fragments.conversationstarter.VFConversationStarterFragmentBuilder
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

        scrollView = findViewById(R.id.article_scroll)

        if (ColorManager.isDarkMode(applicationContext)) {
            scrollView.setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        setupArticleContent()
        addConversationStarterFragment()

        if (preferences.getBoolean(SettingKeys.commentsContainerFullscreen, false)) {
            findViewById<View>(R.id.article_comments_fullscreen).visibility = View.VISIBLE
        } else {
            addCommentsFragment()
        }

        findViewById<View>(R.id.article_comments_fullscreen).setOnClickListener {
            openCommentsContainer()
        }
    }

    private fun setupArticleContent() {
        val story = articleViewModel.story
        val isDarkMode = ColorManager.isDarkMode(applicationContext)
        val primaryTextColor = if (isDarkMode) Color.WHITE else Color.BLACK
        val secondaryTextColor = if (isDarkMode) Color.LTGRAY else Color.DKGRAY

        Glide.with(this)
            .load(story.pictureUrl)
            .into(findViewById<ImageView>(R.id.article_image))

        findViewById<TextView>(R.id.article_category).text = story.category
        findViewById<TextView>(R.id.article_title).apply {
            text = story.title
            setTextColor(primaryTextColor)
        }
        findViewById<TextView>(R.id.article_description).apply {
            text = story.description
            setTextColor(secondaryTextColor)
        }
        findViewById<TextView>(R.id.article_author).apply {
            text = getString(R.string.article_author, story.author)
            setTextColor(secondaryTextColor)
        }

        val splitIndex = ARTICLE_BLOCKS.size / 2
        addArticleBlocks(findViewById(R.id.article_body_top), ARTICLE_BLOCKS.subList(0, splitIndex), primaryTextColor)
        addArticleBlocks(findViewById(R.id.article_body_bottom), ARTICLE_BLOCKS.subList(splitIndex, ARTICLE_BLOCKS.size), primaryTextColor)
    }

    private fun addArticleBlocks(container: LinearLayout, blocks: List<ArticleBlock>, textColor: Int) {
        val spacing = (16 * resources.displayMetrics.density).toInt()
        blocks.forEach { block ->
            val textView = TextView(this)
            textView.setTextColor(textColor)
            when (block) {
                is ArticleBlock.Heading -> {
                    textView.text = block.text
                    textView.textSize = 22f
                    textView.setTypeface(null, android.graphics.Typeface.BOLD)
                }

                is ArticleBlock.Paragraph -> {
                    textView.text = block.text
                    textView.textSize = 17f
                    textView.setLineSpacing(0f, 1.25f)
                }
            }
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.bottomMargin = spacing
            container.addView(textView, params)
        }
    }

    private fun articleMetadata(): VFArticleMetadata {
        val story = articleViewModel.story
        return VFArticleMetadata(story.link, story.title, story.description, story.pictureUrl)
    }

    private fun addConversationStarterFragment() {
        if (supportFragmentManager.findFragmentByTag(TAG_CONVERSATION_STARTER_FRAGMENT) != null) {
            return
        }

        val conversationStarterFragment = VFConversationStarterFragmentBuilder(
            articleViewModel.story.containerId,
            articleMetadata(),
            vfSettings
        ).build()
        conversationStarterFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        supportFragmentManager.beginTransaction()
            .replace(
                R.id.article_conversation_starter_container,
                conversationStarterFragment,
                TAG_CONVERSATION_STARTER_FRAGMENT
            )
            .commitAllowingStateLoss()

        conversationStarterFragment.setLayoutCallback(this)
        conversationStarterFragment.setActionCallback(this)
        conversationStarterFragment.setCustomUICallback(this)
    }

    private fun addCommentsFragment() {
        if (supportFragmentManager.findFragmentByTag(TAG_COMMENTS_FRAGMENT) != null) {
            return
        }

        val story = articleViewModel.story
        val previewCommentsFragment =
            VFPreviewCommentsFragmentBuilder(story.containerId, articleMetadata(), vfSettings)
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

    private fun openCommentsContainer() {
        val intent = Intent(applicationContext, CommentsContainerActivity::class.java)
        intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, articleViewModel.story.containerId)
        startActivity(intent)
    }

    private fun scrollToComments() {
        if (preferences.getBoolean(SettingKeys.commentsContainerFullscreen, false)) {
            openCommentsContainer()
            return
        }

        val yPosition = findViewById<View>(R.id.article_comments_container).y
        scrollView.smoothScrollTo(0, yPosition.toInt())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        val story = articleViewModel.story
        if (actionType == VFActionType.seeMoreCommentsPressed) {
            scrollToComments()
        } else if (actionType == VFActionType.writeNewCommentPressed) {
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
            VFCustomViewType.conversationStarterBackgroundView,
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

        val conversationStarterFragment =
            supportFragmentManager.findFragmentByTag(TAG_CONVERSATION_STARTER_FRAGMENT) as? VFConversationStarterFragment
        conversationStarterFragment?.let {
            it.setLayoutCallback(this)
            it.setActionCallback(this)
            it.setCustomUICallback(this)
        }
    }

    override fun scrollToPosition(position: Int) {
        val yPosition = (findViewById<View>(R.id.article_comments_container).y + position).toInt()
        scrollView.smoothScrollTo(0, yPosition)
    }

    override fun containerHeightUpdated(fragment: VFFragment, containerId: String, height: Int) {
    }

    sealed class ArticleBlock(val text: String) {
        class Heading(text: String) : ArticleBlock(text)
        class Paragraph(text: String) : ArticleBlock(text)
    }

    companion object {
        const val TAG_COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT"
        const val TAG_CONVERSATION_STARTER_FRAGMENT = "CONVERSATION_STARTER_FRAGMENT"

        private val ARTICLE_BLOCKS: List<ArticleBlock> = listOf(
            ArticleBlock.Paragraph("With COVID-19 disrupting the world, the demand for news has never been greater. Newsrooms are being pushed to their limits as they test the most time-saving yet effective methods to sift through an infinite amount of coronavirus information, craft story after story and keep their teams safe."),
            ArticleBlock.Paragraph("According to Therese Bottomly, the editor of a U.S.-based local paper, the \u201Ccoronavirus will strain even the largest newsrooms as news breaks continuously and into the nights and weekends.\u201D"),
            ArticleBlock.Paragraph("So what could be a better way to ease the enormous pressures on your media company than by understanding how other companies are maneuvering through this infodemic?"),
            ArticleBlock.Paragraph("Read on to discover useful ways you can prevent your newsroom staff from burning out while keeping up with the demand for top-quality news."),
            ArticleBlock.Heading("Moving Staff to Cover the Coronavirus"),
            ArticleBlock.Paragraph("It\u2019s no surprise that this health crisis has encouraged consumers to rely on trustworthy news companies for credible coronavirus information. As a result, traffic to news platforms has been soaring over the past few weeks."),
            ArticleBlock.Paragraph("Some media companies are meeting this high demand for news by shifting the focus of all content creators towards the pandemic."),
            ArticleBlock.Paragraph("For example, The Seattle Times is leveraging almost all 58 of its reporters \u2014 who typically focus on different verticals \u2014 to prioritize covering COVID-19 in some way or form."),
            ArticleBlock.Paragraph("Even entertainment-focused brands like Bustle, People.com and BuzzFeed are incorporating coronavirus content across its verticals."),
            ArticleBlock.Paragraph("By encouraging more staff to focus on coronavirus coverage, your newsroom can keep your community informed without burning out."),
            ArticleBlock.Heading("Promoting Content Across News Platforms"),
            ArticleBlock.Paragraph("Before the pandemic hit, it was typically every media company for themselves in the endless pursuit of higher revenue. But priorities have since changed."),
            ArticleBlock.Paragraph("Now, companies are more focused on keeping their newsrooms functional while maintaining an informed and safe audience\u2026 even if that means collaborating with competitors."),
            ArticleBlock.Paragraph("To provide readers with relevant content and prevent editorial teams from being overworked, different media organizations in the U.S. have started repromoting each other\u2019s articles."),
            ArticleBlock.Paragraph("\u201CThe collaboration will allow newsrooms to pick up good information from other sources, so they will not need to re-report the same story,\u201D Bottomly explains. \u201CWe can cover more angles this way.\u201D")
        )
    }
}
