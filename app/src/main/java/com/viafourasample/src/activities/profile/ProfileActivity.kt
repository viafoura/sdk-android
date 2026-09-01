package com.viafourasample.src.activities.profile

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.article.ArticleActivity
import com.viafourasample.src.activities.livequestions.LiveQuestionsActivity
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.profile.VFProfileFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFNotificationPresentationAction
import com.viafourasdk.src.model.local.VFProfilePresentationType
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import com.viafourasdk.src.view.VFUserAvatarView
import java.util.UUID

class ProfileActivity : AppCompatActivity(), VFActionsInterface, VFCustomUIInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        InsetsUtils.applyActionBarInsets(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = resources.getString(R.string.profile)

        addProfileFragment()
    }

    private fun addProfileFragment() {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        val vfSettings = VFSettings(colors)
        var presentationType = VFProfilePresentationType.profile
        val presentationTypeString = intent.getStringExtra(IntentKeys.INTENT_USER_PRESENTATION_TYPE)
        if (presentationTypeString != null) {
            if (presentationTypeString == VFProfilePresentationType.profile.toString()) {
                presentationType = VFProfilePresentationType.profile
            } else if (presentationTypeString == VFProfilePresentationType.feed.toString()) {
                presentationType = VFProfilePresentationType.feed
            }
        }
        val profileFragment = VFProfileFragmentBuilder(
            UUID.fromString(intent.getStringExtra(IntentKeys.INTENT_USER_UUID)),
            presentationType,
            vfSettings
        ).build()
        profileFragment.setActionCallback(this)
        profileFragment.setCustomUICallback(this)
        profileFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.profile_container, profileFragment)
        ft.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        if (actionType == VFActionType.closeProfilePressed) {
            onBackPressed()
        } else if (actionType == VFActionType.notificationPressed) {
            val notification = action.notificationPresentationAction!!
            when (notification.notificationPresentationType) {
                VFNotificationPresentationAction.VFNotificationPresentationType.profile -> {
                    val intent = Intent(applicationContext, ProfileActivity::class.java)
                    intent.putExtra(IntentKeys.INTENT_USER_UUID, notification.userUUID.toString())
                    startActivity(intent)
                }

                VFNotificationPresentationAction.VFNotificationPresentationType.content -> {
                    val intent = Intent(applicationContext, ArticleActivity::class.java)
                    intent.putExtra(
                        IntentKeys.INTENT_CONTAINER_ID,
                        notification.containerId.toString()
                    )
                    intent.putExtra(
                        IntentKeys.INTENT_FOCUS_CONTENT_UUID,
                        notification.contentUUID.toString()
                    )
                    startActivity(intent)
                }

                VFNotificationPresentationAction.VFNotificationPresentationType.liveQuestions -> {
                    val intent = Intent(applicationContext, LiveQuestionsActivity::class.java)
                    intent.putExtra(IntentKeys.INTENT_CONTAINER_ID, notification.containerId)
                    intent.putExtra(
                        IntentKeys.INTENT_FOCUS_CONTENT_UUID,
                        notification.contentUUID.toString()
                    )
                    notification.articleMetadata?.let {
                        intent.putExtra(IntentKeys.INTENT_STORY_TITLE, it.title)
                    }
                    startActivity(intent)
                }

                VFNotificationPresentationAction.VFNotificationPresentationType.url -> {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(notification.url)))
                }

                else -> {}
            }
        } else if (actionType == VFActionType.authPressed) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: View) {
        when (customViewType) {
            VFCustomViewType.profileUserAvatar -> {
                if (theme == VFTheme.dark) {
                    (view as VFUserAvatarView).setInitialsTextColor(Color.GREEN)
                }
            }

            else -> {}
        }
    }
}
