package com.viafourasample.src.activities.livechat

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.model.SettingKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.livechat.VFLiveChatFragment
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme

class LiveChatVideoActivity : AppCompatActivity(), VFActionsInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_chat_video)

        InsetsUtils.enableEdgeToEdge(this)

        supportActionBar!!.hide()

        title = intent.getStringExtra(IntentKeys.INTENT_STORY_TITLE)

        setupClose()
        setupVideo()
        setupGradient()
        setupLiveChatWidget()
    }

    private fun setupClose() {
        findViewById<View>(R.id.live_chat_close).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupVideo() {
        val videoView = findViewById<VideoView>(R.id.live_chat_video)

        val path = "android.resource://$packageName/${R.raw.livechat_video}"
        videoView.setVideoURI(Uri.parse(path))
        videoView.requestFocus()
        videoView.start()

        videoView.setOnPreparedListener { mp -> mp.isLooping = true }
    }

    private fun setupLiveChatWidget() {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        val vfSettings = VFSettings(colors)
        val siteDomain = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN)
        val siteUrl = "https://$siteDomain"
        val metadata = VFArticleMetadata(
            siteUrl,
            intent.getStringExtra(IntentKeys.INTENT_STORY_TITLE)!!,
            "",
            siteUrl
        )
        val liveChatFragment = VFLiveChatFragment.newInstance(
            intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID),
            metadata,
            vfSettings
        )
        liveChatFragment.setTheme(VFTheme.dark)
        liveChatFragment.setActionCallback(this)
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.live_chat_container, liveChatFragment)
        ft.commit()
    }

    private fun setupGradient() {
        val gd = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(Color.TRANSPARENT, Color.BLACK, Color.BLACK)
        )

        findViewById<View>(R.id.live_chat_container).background = gd
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        if (actionType == VFActionType.openProfilePressed) {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            intent.putExtra(
                IntentKeys.INTENT_USER_UUID,
                action.openProfileAction!!.userUUID.toString()
            )
            startActivity(intent)
        } else if (actionType == VFActionType.authPressed) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }
}
