package com.viafourasample.src.activities.poll

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.base.VFFragment
import com.viafourasdk.src.fragments.polls.VFPollFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFLayoutInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import com.viafourasdk.src.utils.VFInsetsUtils
import java.util.UUID

class PollActivity : AppCompatActivity(), VFActionsInterface, VFLayoutInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poll)

        InsetsUtils.applyActionBarInsets(this)
        VFInsetsUtils.applyBottomInsetsToScrollable(findViewById<View>(R.id.poll_scroll))

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = intent.getStringExtra(IntentKeys.INTENT_STORY_TITLE) ?: "Poll"

        if (ColorManager.isDarkMode(applicationContext)) {
            findViewById<View>(R.id.poll_scroll).setBackgroundColor(
                ContextCompat.getColor(applicationContext, R.color.colorBackgroundArticle)
            )
        }

        addPollFragment()
    }

    private fun addPollFragment() {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorVfDark),
            ContextCompat.getColor(applicationContext, R.color.colorVf)
        )
        val vfSettings = VFSettings(colors)
        val contentContainerUUID = UUID.fromString(intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID))

        val pollFragment = VFPollFragmentBuilder(contentContainerUUID, vfSettings).build()
        pollFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        pollFragment.setActionCallback(this)
        pollFragment.setLayoutCallback(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.poll_container, pollFragment)
            .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
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

    override fun containerHeightUpdated(fragment: VFFragment, containerId: String, height: Int) {
    }
}
