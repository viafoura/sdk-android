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
import com.viafourasample.src.model.Story
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

@Composable
fun CommentsScreen(
    story: Story,
    fragmentManager: FragmentManager,
    onBack: () -> Unit,
    onOpenNewComment: (OpenNewCommentArgs) -> Unit,
    onOpenProfile: (OpenProfileArgs) -> Unit,
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
                title = { Text(text = "Comments") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
            fragmentTag = "vf_comments_${story.containerId}",
            modifier = Modifier.fillMaxSize(),
            createFragment = {
                val metadata = VFArticleMetadata(story.link, story.title, story.description, story.pictureUrl)
                VFPreviewCommentsFragmentBuilder(story.containerId, metadata, vfSettings).build()
            },
            onFragmentAvailable = { fragment ->
                val vfFragment = fragment as? VFPreviewCommentsFragment ?: return@FragmentHost
                vfFragment.setTheme(if (ColorManager.isDarkMode(context)) VFTheme.dark else VFTheme.light)

                vfFragment.setCustomUICallback(object : VFCustomUIInterface {
                    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: android.view.View) {
                        if (theme == VFTheme.dark && customViewType == VFCustomViewType.previewBackgroundView) {
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundArticle))
                        }
                    }
                })

                vfFragment.setActionCallback(object : VFActionsInterface {
                    override fun onNewAction(actionType: VFActionType, action: VFActionData) {
                        when (actionType) {
                            VFActionType.writeNewCommentPressed -> {
                                val newCommentAction = action.newCommentAction ?: return
                                onOpenNewComment(
                                    OpenNewCommentArgs(
                                        containerId = story.containerId,
                                        containerType = if (story.storyType == Story.StoryType.reviews) com.viafourasdk.src.model.local.VFCommentsContainerType.reviews else com.viafourasdk.src.model.local.VFCommentsContainerType.conversations,
                                        actionType = newCommentAction.type,
                                        contentUuid = newCommentAction.content?.toString(),
                                        link = story.link,
                                        title = story.title,
                                        desc = story.description,
                                        pic = story.pictureUrl
                                    )
                                )
                            }
                            VFActionType.openProfilePressed -> {
                                val openProfileAction = action.openProfileAction ?: return
                                val presentationType = openProfileAction.presentationType?.toString()
                                onOpenProfile(OpenProfileArgs(openProfileAction.userUUID.toString(), presentationType))
                            }
                            VFActionType.authPressed -> {
                                onAuth(activity)
                            }
                            else -> {}
                        }
                    }
                })
            }
        )
    }
}
