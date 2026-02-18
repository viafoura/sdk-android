package com.viafourasample.src.compose

import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.FragmentContainerView

@Composable
fun FragmentHost(
    fragmentManager: FragmentManager,
    fragmentTag: String,
    modifier: Modifier = Modifier,
    createFragment: () -> Fragment,
    onFragmentAvailable: (Fragment) -> Unit = {}
) {
    val containerId = remember { View.generateViewId() }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            FragmentContainerView(context).apply {
                id = containerId
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        },
        update = {
            val existing = fragmentManager.findFragmentByTag(fragmentTag)
            if (existing == null) {
                val fragment = createFragment()
                val ft: FragmentTransaction = fragmentManager.beginTransaction()
                ft.replace(containerId, fragment, fragmentTag)
                ft.commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
                fragmentManager.findFragmentByTag(fragmentTag)?.let(onFragmentAvailable)
            } else {
                onFragmentAvailable(existing)
            }
        }
    )

    DisposableEffect(fragmentManager, fragmentTag) {
        onDispose {
            val existing = fragmentManager.findFragmentByTag(fragmentTag)
            if (existing != null) {
                fragmentManager.beginTransaction().remove(existing).commitAllowingStateLoss()
            }
        }
    }
}
