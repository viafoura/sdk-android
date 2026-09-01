package com.viafourasample.src.activities.newcomment

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.managers.ColorManager
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCommentsContainerType
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFNewCommentAction
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import java.util.UUID

class NewCommentActivity : AppCompatActivity(), VFActionsInterface, VFCustomUIInterface {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_comment)

        InsetsUtils.applyActionBarInsets(this)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar!!.title = "Nuevo comentario"

        addNewCommentsFragment()
    }

    private fun addNewCommentsFragment() {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        val vfSettings = VFSettings(colors)
        val articleMetadata = VFArticleMetadata(
            intent.getStringExtra(IntentKeys.INTENT_STORY_LINK)!!,
            intent.getStringExtra(IntentKeys.INTENT_STORY_TITLE)!!,
            intent.getStringExtra(IntentKeys.INTENT_STORY_DESC)!!,
            intent.getStringExtra(IntentKeys.INTENT_STORY_PICTUREURL)!!
        )

        val newCommentActionType = intent.getStringExtra(IntentKeys.INTENT_NEW_COMMENT_ACTION)
        val containerTypeValue = intent.getStringExtra(IntentKeys.INTENT_CONTAINER_TYPE)

        val newCommentAction = when (newCommentActionType) {
            VFNewCommentAction.VFNewCommentActionType.create.toString() ->
                VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.create)

            VFNewCommentAction.VFNewCommentActionType.reply.toString() ->
                VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.reply)

            VFNewCommentAction.VFNewCommentActionType.edit.toString() ->
                VFNewCommentAction(VFNewCommentAction.VFNewCommentActionType.edit)

            else -> null
        }

        @Suppress("UNUSED_VARIABLE")
        val containerType = when (containerTypeValue) {
            VFCommentsContainerType.reviews.toString() -> VFCommentsContainerType.reviews
            else -> VFCommentsContainerType.conversations
        }

        intent.getStringExtra(IntentKeys.INTENT_NEW_COMMENT_CONTENT)?.let {
            newCommentAction!!.content = UUID.fromString(it)
        }

        val newCommentFragment = VFNewCommentFragmentBuilder(
            newCommentAction!!,
            intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID)!!,
            articleMetadata,
            vfSettings
        ).build()

        newCommentFragment.setActionCallback(this)
        newCommentFragment.setCustomUICallback(this)
        newCommentFragment.setTheme(
            if (ColorManager.isDarkMode(applicationContext)) VFTheme.dark else VFTheme.light
        )
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.new_comment_container, newCommentFragment)
        ft.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
        if (actionType == VFActionType.closeNewCommentPressed) {
            onBackPressed()
        } else if (actionType == VFActionType.authPressed) {
            startActivity(Intent(applicationContext, LoginActivity::class.java))
        }
    }

    override fun customizeView(theme: VFTheme, viewType: VFCustomViewType, view: View) {
    }
}
