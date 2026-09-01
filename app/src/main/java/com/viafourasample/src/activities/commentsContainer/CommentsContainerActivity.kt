package com.viafourasample.src.activities.commentsContainer

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.newcomment.NewCommentActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragment
import com.viafourasdk.src.fragments.previewcomments.VFPreviewCommentsFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import com.viafourasdk.src.utils.VFInsetsUtils

class CommentsContainerActivity : AppCompatActivity(), VFActionsInterface, VFCustomUIInterface {
    private lateinit var vfSettings: VFSettings
    private lateinit var commentsContainerViewModel: CommentsContainerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments_container)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsToScrollable(findViewById<View>(R.id.comments_container_scroll))

        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        vfSettings = VFSettings(colors)

        commentsContainerViewModel =
            CommentsContainerViewModel(intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID)!!)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Comments"

        if (ColorManager.isDarkMode(applicationContext)) {
            findViewById<View>(R.id.comments_container_scroll).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        addCommentsFragment()
    }

    private fun addCommentsFragment() {
        val story = commentsContainerViewModel.story
        val articleMetadata =
            VFArticleMetadata(story.link, story.title, story.description, story.pictureUrl)
        val previewCommentsFragment =
            VFPreviewCommentsFragmentBuilder(story.containerId, articleMetadata, vfSettings).build()
        previewCommentsFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.comments_container, previewCommentsFragment, TAG_COMMENTS_FRAGMENT)
        ft.commitAllowingStateLoss()

        previewCommentsFragment.setCustomUICallback(this)
        previewCommentsFragment.setActionCallback(this)
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        val story = commentsContainerViewModel.story
        if (actionType == VFActionType.writeNewCommentPressed) {
            val newCommentAction = action.newCommentAction!!
            val intent = Intent(applicationContext, NewCommentActivity::class.java)
            intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, story.containerId)
            intent.putExtra(IntentKeys.INTENT_STORY_LINK, story.link)
            intent.putExtra(IntentKeys.INTENT_STORY_TITLE, story.title)
            intent.putExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION, newCommentAction.type.toString())
            intent.putExtra(IntentKeys.INTENT_CONTAINER_TYPE, story.storyType.toString())
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
        } else if (actionType == VFActionType.authPressed) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

        val commentsFragment =
            supportFragmentManager.findFragmentByTag(TAG_COMMENTS_FRAGMENT) as? VFPreviewCommentsFragment
        commentsFragment?.let {
            it.setActionCallback(this)
            it.setCustomUICallback(this)
        }
    }

    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: View) {
        when (customViewType) {
            VFCustomViewType.previewBackgroundView -> {
                if (theme == VFTheme.dark) {
                    view.setBackgroundColor(
                        ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
                    )
                }
            }

            else -> {}
        }
    }

    companion object {
        const val TAG_COMMENTS_FRAGMENT = "COMMENTS_FRAGMENT"
    }
}
