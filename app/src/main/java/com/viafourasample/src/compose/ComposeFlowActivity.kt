package com.viafourasample.src.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import com.viafourasample.src.managers.StoryManager
import com.viafourasample.src.model.IntentKeys

class ComposeFlowActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val containerId = intent.getStringExtra(IntentKeys.INTENT_CONTAINER_ID)
            ?: StoryManager.getInstance().storyList.firstOrNull()?.containerId
            ?: ""

        setContent {
            ComposeFlowApp(
                startContainerId = containerId,
                fragmentManager = supportFragmentManager,
                onFinish = { finish() }
            )
        }
    }
}
