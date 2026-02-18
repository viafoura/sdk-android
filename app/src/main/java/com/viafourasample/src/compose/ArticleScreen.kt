package com.viafourasample.src.compose

import android.annotation.SuppressLint
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.viewinterop.AndroidView
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
import com.viafourasdk.src.interfaces.VFContentScrollPositionInterface
import com.viafourasdk.src.interfaces.VFCustomUIInterface
import com.viafourasdk.src.model.local.VFActionData
import com.viafourasdk.src.model.local.VFActionType
import com.viafourasdk.src.model.local.VFArticleMetadata
import com.viafourasdk.src.model.local.VFColors
import com.viafourasdk.src.model.local.VFCustomViewType
import com.viafourasdk.src.model.local.VFNotificationPresentationAction
import com.viafourasdk.src.model.local.VFProfilePresentationType
import com.viafourasdk.src.model.local.VFSettings
import com.viafourasdk.src.model.local.VFSortType
import com.viafourasdk.src.model.local.VFTheme
import java.util.Collections

@Composable
fun ArticleScreen(
    story: Story,
    fragmentManager: FragmentManager,
    onBack: () -> Unit,
    onOpenComments: (String) -> Unit,
    onOpenNewComment: (OpenNewCommentArgs) -> Unit,
    onOpenProfile: (OpenProfileArgs) -> Unit,
    onOpenArticle: (String) -> Unit,
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

    val webViewId = remember { View.generateViewId() }
    val commentsContainerId = remember { View.generateViewId() }
    val commentsTag = remember(story.containerId) { "vf_preview_${story.containerId}" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = story.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                actions = {
                    IconButton(onClick = { onOpenComments(story.containerId) }) {
                        Text(text = "Comments")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            factory = { ctx ->
                val scrollView = ScrollView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }

                val content = LinearLayout(ctx).apply {
                    orientation = LinearLayout.VERTICAL
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                val webView = WebView(ctx).apply {
                    id = webViewId
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = object : WebViewClient() {
                        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                            return request.url.toString() != story.link
                        }

                        override fun onPageFinished(view: WebView, url: String) {
                            if (ColorManager.isDarkMode(ctx)) {
                                view.evaluateJavascript("document.documentElement.classList.add('dark');", null)
                            }
                            view.post {
                                val h = (view.contentHeight * view.scale).toInt()
                                if (h > 0) {
                                    view.layoutParams.height = h
                                    view.requestLayout()
                                }
                            }
                        }
                    }
                    loadUrl(story.link)
                }

                val commentsContainer = androidx.fragment.app.FragmentContainerView(ctx).apply {
                    id = commentsContainerId
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                content.addView(webView)
                content.addView(commentsContainer)
                scrollView.addView(content)
                scrollView
            },
            update = { scrollView ->
                val webView = scrollView.findViewById<WebView>(webViewId)
                if (webView.url != story.link) {
                    webView.loadUrl(story.link)
                }

                val existing = fragmentManager.findFragmentByTag(commentsTag)
                if (existing == null) {
                    val metadata = VFArticleMetadata(story.link, story.title, story.description, story.pictureUrl)
                    val fragment = VFPreviewCommentsFragmentBuilder(story.containerId, metadata, vfSettings)
                        .paginationSize(10)
                        .replySize(10)
                        .sortType(VFSortType.newest)
                        .build()

                    fragmentManager.beginTransaction()
                        .replace(commentsContainerId, fragment, commentsTag)
                        .commitAllowingStateLoss()
                    fragmentManager.executePendingTransactions()
                }

                val vfFragment = fragmentManager.findFragmentByTag(commentsTag) as? VFPreviewCommentsFragment ?: return@AndroidView
                vfFragment.setTheme(if (ColorManager.isDarkMode(context)) VFTheme.dark else VFTheme.light)
                vfFragment.setAuthorIds(Collections.singletonList("3147700024522"))

                vfFragment.setCustomUICallback(object : VFCustomUIInterface {
                    override fun customizeView(theme: VFTheme, customViewType: VFCustomViewType, view: android.view.View) {
                        if (
                            theme == VFTheme.dark &&
                            (customViewType == VFCustomViewType.previewBackgroundView ||
                                customViewType == VFCustomViewType.trendingVerticalBackground ||
                                customViewType == VFCustomViewType.trendingCarouselBackground)
                        ) {
                            view.setBackgroundColor(ContextCompat.getColor(context, R.color.colorBackgroundArticle))
                        }
                    }
                })

                vfFragment.setScrollPositionCallback(object : VFContentScrollPositionInterface {
                    override fun scrollToPosition(position: Int) {
                        val container = scrollView.findViewById<View>(commentsContainerId)
                        scrollView.smoothScrollTo(0, container.top + position)
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
                            VFActionType.notificationPressed -> {
                                val n = action.notificationPresentationAction ?: return
                                if (n.notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.profile) {
                                    onOpenProfile(OpenProfileArgs(n.userUUID.toString(), VFProfilePresentationType.profile.toString()))
                                } else if (n.notificationPresentationType == VFNotificationPresentationAction.VFNotificationPresentationType.content) {
                                    onOpenArticle(n.containerId.toString())
                                }
                            }
                            VFActionType.trendingArticlePressed -> {
                                val trendingPressedAction = action.trendingPressedAction ?: return
                                onOpenArticle(trendingPressedAction.containerId)
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
