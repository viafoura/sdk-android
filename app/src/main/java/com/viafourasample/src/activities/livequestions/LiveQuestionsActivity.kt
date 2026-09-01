package com.viafourasample.src.activities.livequestions

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.activities.profile.ProfileActivity
import com.viafourasample.src.model.IntentKeys
import com.viafourasample.src.model.SettingKeys
import com.viafourasample.src.utils.InsetsUtils
import com.viafourasdk.src.fragments.base.VFFragment
import com.viafourasdk.src.fragments.livequestions.VFLiveQuestionsComposerFragment
import com.viafourasdk.src.fragments.livequestions.VFLiveQuestionsFragment
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFLayoutInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFNewQuestionAction
import com.viafourasdk.src.model.local.VFSettings

class LiveQuestionsActivity : AppCompatActivity(), VFActionsInterface, VFLayoutInterface {

    private var containerId: String? = null
    private var storyTitle: String? = null
    private var focusedContentUUID: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_questions)

        InsetsUtils.applyActionBarInsets(this)

        storyTitle = intent.getStringExtra(IntentKeys.INTENT_STORY_TITLE)
        containerId = intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID)
        focusedContentUUID = intent.getStringExtra(IntentKeys.INTENT_FOCUS_CONTENT_UUID)
        storyTitle?.let { title = it }

        loadFragment(containerId)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun buildSettings(): VFSettings {
        val colors = VFColors(
            ContextCompat.getColor(applicationContext, R.color.colorPrimary),
            ContextCompat.getColor(applicationContext, R.color.colorPrimaryLight)
        )
        return VFSettings(colors)
    }

    private fun buildArticleMetadata(): VFArticleMetadata {
        val siteDomain = PreferenceManager.getDefaultSharedPreferences(applicationContext)
            .getString(SettingKeys.siteDomain, SettingKeys.DEFAULT_SITE_DOMAIN)
        val siteUrl = "https://$siteDomain"
        val articleUrl = "$siteUrl/${SettingKeys.LIVE_QUESTIONS_ARTICLE_PATH}"
        return VFArticleMetadata(
            articleUrl,
            storyTitle ?: "Live Questions",
            "",
            siteUrl
        )
    }

    private fun loadFragment(id: String?) {
        val vfSettings = buildSettings()
        val metadata = buildArticleMetadata()

        val fragment = VFLiveQuestionsFragment.newInstance(
            id ?: "test-livequestions",
            metadata,
            vfSettings,
            8,
            2,
            null,
            focusedContentUUID
        )
        fragment.actionsInterface = this
        fragment.setLayoutCallback(this)

        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.live_questions_container, fragment)
        ft.commit()
    }

    private fun showChangeIdDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Live Q&A container ID")
        builder.setMessage("Enter a container ID for Live Q&A")

        val input = EditText(this)
        input.hint = "ID"
        input.setText(containerId)

        val focusedInput = EditText(this)
        focusedInput.hint = "focusedContentUUID (optional)"
        focusedInput.setText(focusedContentUUID)

        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        val padding = (16 * resources.displayMetrics.density).toInt()
        layout.setPadding(padding, 0, padding, 0)
        layout.addView(input)
        layout.addView(focusedInput)
        builder.setView(layout)

        builder.setPositiveButton("Accept") { _, _ ->
            val newId = input.text.toString().trim()
            if (newId.isNotEmpty()) {
                containerId = newId
                val newFocusedContentUUID = focusedInput.text.toString().trim()
                focusedContentUUID = newFocusedContentUUID.ifEmpty { null }
                loadFragment(containerId)
            }
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_live_questions, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        } else if (item.itemId == R.id.action_change_id) {
            showChangeIdDialog()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun containerHeightUpdated(fragment: VFFragment, containerId: String, height: Int) {
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
        } else if (actionType == VFActionType.writeNewQuestionPressed) {
            presentComposer(action.newQuestionAction)
        }
    }

    private fun presentComposer(newQuestionAction: VFNewQuestionAction?) {
        if (newQuestionAction == null) {
            return
        }

        val composer = VFLiveQuestionsComposerFragment.newInstance(
            newQuestionAction,
            containerId ?: "test-livequestions",
            buildArticleMetadata(),
            buildSettings()
        )
        composer.actionsInterface = this
        composer.show(supportFragmentManager, "composer")
    }
}
