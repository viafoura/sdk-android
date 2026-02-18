package com.viafourasample.src.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.viafoura.sampleapp.R
import com.viafourasample.src.activities.login.LoginActivity
import com.viafourasample.src.managers.StoryManager
import com.viafourasdk.src.model.local.VFCommentsContainerType
import com.viafourasdk.src.model.local.VFNewCommentAction

private object Routes {
    const val Article = "article"
    const val Comments = "comments"
    const val NewComment = "new_comment"
    const val Profile = "profile"
}

@Composable
fun ComposeFlowApp(
    startContainerId: String,
    fragmentManager: FragmentManager,
    onFinish: () -> Unit
) {
    val navController = rememberNavController()

    val primary = colorResource(id = R.color.colorPrimary)
    val onPrimary = colorResource(id = R.color.white)
    MaterialTheme(
        colors = lightColors(
            primary = primary,
            primaryVariant = primary,
            secondary = primary,
            onPrimary = onPrimary,
            onSecondary = onPrimary,
            surface = Color.White,
            background = Color.White,
            onSurface = Color.Black,
            onBackground = Color.Black
        )
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
            NavHost(
                navController = navController,
                startDestination = "${Routes.Article}/{containerId}"
            ) {
                composable(
                    route = "${Routes.Article}/{containerId}",
                    arguments = listOf(navArgument("containerId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val containerId = backStackEntry.arguments?.getString("containerId") ?: startContainerId
                    val story = remember(containerId) { StoryManager.getInstance().getStory(containerId) }

                    ArticleScreen(
                        story = story,
                        fragmentManager = fragmentManager,
                        onBack = onFinish,
                        onOpenComments = { navController.navigate("${Routes.Comments}/${encode(it)}") },
                        onOpenNewComment = { args ->
                            navController.navigate(
                                "${Routes.NewComment}?" +
                                    "containerId=${encode(args.containerId)}&" +
                                    "containerType=${encode(args.containerType.toString())}&" +
                                    "actionType=${encode(args.actionType.toString())}&" +
                                    "contentUuid=${encode(args.contentUuid ?: "")}&" +
                                    "link=${encode(args.link)}&" +
                                    "title=${encode(args.title)}&" +
                                    "desc=${encode(args.desc)}&" +
                                    "pic=${encode(args.pic)}"
                            )
                        },
                        onOpenProfile = { args ->
                            navController.navigate(
                                "${Routes.Profile}?" +
                                    "userUuid=${encode(args.userUuid)}&" +
                                    "presentationType=${encode(args.presentationType ?: "")}"
                            )
                        },
                        onOpenArticle = { navController.navigate("${Routes.Article}/${encode(it)}") },
                        onAuth = { activity ->
                            activity.startActivity(Intent(activity, LoginActivity::class.java))
                        }
                    )
                }

                composable(
                    route = "${Routes.Comments}/{containerId}",
                    arguments = listOf(navArgument("containerId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val containerId = backStackEntry.arguments?.getString("containerId") ?: startContainerId
                    val story = remember(containerId) { StoryManager.getInstance().getStory(containerId) }

                    CommentsScreen(
                        story = story,
                        fragmentManager = fragmentManager,
                        onBack = { navController.popBackStack() },
                        onOpenNewComment = { args ->
                            navController.navigate(
                                "${Routes.NewComment}?" +
                                    "containerId=${encode(args.containerId)}&" +
                                    "containerType=${encode(args.containerType.toString())}&" +
                                    "actionType=${encode(args.actionType.toString())}&" +
                                    "contentUuid=${encode(args.contentUuid ?: "")}&" +
                                    "link=${encode(args.link)}&" +
                                    "title=${encode(args.title)}&" +
                                    "desc=${encode(args.desc)}&" +
                                    "pic=${encode(args.pic)}"
                            )
                        },
                        onOpenProfile = { args ->
                            navController.navigate(
                                "${Routes.Profile}?" +
                                    "userUuid=${encode(args.userUuid)}&" +
                                    "presentationType=${encode(args.presentationType ?: "")}"
                            )
                        },
                        onAuth = { activity ->
                            activity.startActivity(Intent(activity, LoginActivity::class.java))
                        }
                    )
                }

                composable(
                    route = "${Routes.NewComment}?containerId={containerId}&containerType={containerType}&actionType={actionType}&contentUuid={contentUuid}&link={link}&title={title}&desc={desc}&pic={pic}",
                    arguments = listOf(
                        navArgument("containerId") { type = NavType.StringType; defaultValue = "" },
                        navArgument("containerType") { type = NavType.StringType; defaultValue = VFCommentsContainerType.conversations.toString() },
                        navArgument("actionType") { type = NavType.StringType; defaultValue = VFNewCommentAction.VFNewCommentActionType.create.toString() },
                        navArgument("contentUuid") { type = NavType.StringType; defaultValue = "" },
                        navArgument("link") { type = NavType.StringType; defaultValue = "" },
                        navArgument("title") { type = NavType.StringType; defaultValue = "" },
                        navArgument("desc") { type = NavType.StringType; defaultValue = "" },
                        navArgument("pic") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val containerId = backStackEntry.arguments?.getString("containerId") ?: ""
                    val containerType = backStackEntry.arguments?.getString("containerType") ?: VFCommentsContainerType.conversations.toString()
                    val actionType = backStackEntry.arguments?.getString("actionType") ?: VFNewCommentAction.VFNewCommentActionType.create.toString()
                    val contentUuid = backStackEntry.arguments?.getString("contentUuid").orEmpty().ifBlank { null }
                    val link = backStackEntry.arguments?.getString("link").orEmpty()
                    val title = backStackEntry.arguments?.getString("title").orEmpty()
                    val desc = backStackEntry.arguments?.getString("desc").orEmpty()
                    val pic = backStackEntry.arguments?.getString("pic").orEmpty()

                    NewCommentScreen(
                        args = NewCommentArgs(
                            containerId = containerId,
                            containerType = parseContainerType(containerType),
                            actionType = parseNewCommentActionType(actionType),
                            contentUuid = contentUuid,
                            link = link,
                            title = title,
                            desc = desc,
                            pic = pic
                        ),
                        fragmentManager = fragmentManager,
                        onClose = { navController.popBackStack() },
                        onAuth = { activity ->
                            activity.startActivity(Intent(activity, LoginActivity::class.java))
                        }
                    )
                }

                composable(
                    route = "${Routes.Profile}?userUuid={userUuid}&presentationType={presentationType}",
                    arguments = listOf(
                        navArgument("userUuid") { type = NavType.StringType; defaultValue = "" },
                        navArgument("presentationType") { type = NavType.StringType; defaultValue = "" }
                    )
                ) { backStackEntry ->
                    val userUuid = backStackEntry.arguments?.getString("userUuid").orEmpty()
                    val presentationType = backStackEntry.arguments?.getString("presentationType").orEmpty().ifBlank { null }

                    ProfileScreen(
                        args = ProfileArgs(userUuid = userUuid, presentationType = presentationType),
                        fragmentManager = fragmentManager,
                        onClose = { navController.popBackStack() },
                        onAuth = { activity ->
                            activity.startActivity(Intent(activity, LoginActivity::class.java))
                        },
                        onOpenArticle = { navController.navigate("${Routes.Article}/${encode(it)}") },
                        onOpenProfile = { args ->
                            navController.navigate(
                                "${Routes.Profile}?" +
                                    "userUuid=${encode(args.userUuid)}&" +
                                    "presentationType=${encode(args.presentationType ?: "")}"
                            )
                        }
                    )
                }
            }
        }
    }
}

private fun encode(value: String): String = Uri.encode(value)

private fun parseContainerType(value: String): VFCommentsContainerType {
    return if (value == VFCommentsContainerType.reviews.toString()) VFCommentsContainerType.reviews else VFCommentsContainerType.conversations
}

private fun parseNewCommentActionType(value: String): VFNewCommentAction.VFNewCommentActionType {
    return when (value) {
        VFNewCommentAction.VFNewCommentActionType.reply.toString() -> VFNewCommentAction.VFNewCommentActionType.reply
        VFNewCommentAction.VFNewCommentActionType.edit.toString() -> VFNewCommentAction.VFNewCommentActionType.edit
        else -> VFNewCommentAction.VFNewCommentActionType.create
    }
}

data class NewCommentArgs(
    val containerId: String,
    val containerType: VFCommentsContainerType,
    val actionType: VFNewCommentAction.VFNewCommentActionType,
    val contentUuid: String?,
    val link: String,
    val title: String,
    val desc: String,
    val pic: String
)

data class ProfileArgs(
    val userUuid: String,
    val presentationType: String?
)

data class OpenNewCommentArgs(
    val containerId: String,
    val containerType: VFCommentsContainerType,
    val actionType: VFNewCommentAction.VFNewCommentActionType,
    val contentUuid: String?,
    val link: String,
    val title: String,
    val desc: String,
    val pic: String
)

data class OpenProfileArgs(
    val userUuid: String,
    val presentationType: String?
)
