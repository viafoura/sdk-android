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
import com.viafourasdk.src.fragments.profile.VFProfileFragment
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
import java.util.UUID

@Composable
fun ProfileScreen(
    args: ProfileArgs,
    fragmentManager: FragmentManager,
    onClose: () -> Unit,
    onAuth: (android.app.Activity) -> Unit,
    onOpenArticle: (String) -> Unit,
    onOpenProfile: (OpenProfileArgs) -> Unit
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
                title = { Text(text = "Profile") },
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
            fragmentTag = "vf_profile_${args.userUuid}",
            modifier = Modifier.fillMaxSize(),
            createFragment = {
                val presentationType = if (args.presentationType == VFProfilePresentationType.feed.toString()) {
                    VFProfilePresentationType.feed
                } else {
                    VFProfilePresentationType.profile
                }
                VFProfileFragmentBuilder(UUID.fromString(args.userUuid), presentationType, vfSettings).build()
            },
            onFragmentAvailable = { fragment ->
                val vfFragment = fragment as? VFProfileFragment ?: return@FragmentHost
                vfFragment.setTheme(if (ColorManager.isDarkMode(context)) VFTheme.dark else VFTheme.light)
                vfFragment.setCustomUICallback(object : VFCustomUIInterface {
                    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: android.view.View) {}
                })
                vfFragment.setActionCallback(object : VFActionsInterface {
                    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
                        when (actionType) {
                            VFActionType.closeProfilePressed -> onClose()
                            VFActionType.notificationPressed -> {
                                val n = action.notificationPresentationAction ?: return
                                if (n.notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.profile) {
                                    onOpenProfile(OpenProfileArgs(n.userUUID.toString(), VFProfilePresentationType.profile.toString()))
                                } else if (n.notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.content) {
                                    onOpenArticle(n.containerId.toString())
                                }
                            }
                            VFActionType.authPressed -> onAuth(activity)
                            else -> {}
                        }
                    }
                })
            }
        )
    }
}
