package com.viafourasample.src.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.viafoura.sampleapp.R
import com.viafourasample.src.managers.ColorManager
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragment
import com.viafourasdk.src.fragments.newcomment.VFNewCommentFragmentBuilder
import com.viafourasdk.src.interfaces.VFActionsInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFNewCommentAction
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFTheme
import java.util.UUID

@Composable
fun NewCommentScreen(
    args: NewCommentArgs,
    fragmentManager: FragmentManager,
    onClose: () -> Unit,
    onAuth: (android.app.Activity) -> Unit
) {
    val context = LocalContext.current
    val activity = context as android.app.Activity

    val vfSettings = remember {
        val colors = VFColors(
            ContextCompat.getColor(context, R.color.colorVfDark),
            ContextCompat.getColor(context, R.color.colorVf)
        )
        VFSettings(colors)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "New comment") },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) {
        FragmentHost(
            fragmentManager = fragmentManager,
            fragmentTag = "vf_new_comment_${args.containerId}",
            modifier = Modifier.fillMaxSize(),
            createFragment = {
                val metadata = VFArticleMetadata(args.link, args.title, args.desc, args.pic)
                val action = VFNewCommentAction(args.actionType).apply {
                    if (!args.contentUuid.isNullOrBlank()) {
                        content = UUID.fromString(args.contentUuid)
                    }
                }
                VFNewCommentFragmentBuilder(action, args.containerId, metadata, vfSettings)
                    .containerType(args.containerType)
                    .build()
            },
            onFragmentAvailable = { fragment ->
                val vfFragment = fragment as? VFNewCommentFragment ?: return@FragmentHost
                vfFragment.setTheme(if (ColorManager.isDarkMode(context)) VFTheme.dark else VFTheme.light)

                vfFragment.setCustomUICallback(object : VFCustomUIInterface {
                    override fun customizeView(theme: VFTheme, viewType: VFCustomViewType, view: android.view.View) {}
                })

                vfFragment.setActionCallback(object : VFActionsInterface {
                    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
                        when (actionType) {
                            VFActionType.closeNewCommentPressed -> onClose()
                            VFActionType.authPressed -> onAuth(activity)
                            else -> {}
                        }
                    }
                })
            }
        )
    }
}
