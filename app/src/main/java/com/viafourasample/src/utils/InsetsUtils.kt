package com.viafourasample.src.utils

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.viafoura.sampleapp.R

object InsetsUtils {

    private val SYSTEM_BARS =
        WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()

    fun enableEdgeToEdge(activity: AppCompatActivity) {
        val window = activity.window
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView)
            .isAppearanceLightStatusBars = false
    }

    fun applyActionBarInsets(activity: AppCompatActivity) {
        enableEdgeToEdge(activity)

        val decor = activity.window.decorView as? FrameLayout ?: return

        val existingScrim: View? = decor.findViewById(R.id.status_bar_scrim)
        val scrim = existingScrim ?: View(activity).also { view ->
            view.id = R.id.status_bar_scrim
            view.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorToolbar))
            decor.addView(
                view,
                0,
                FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, 0, Gravity.TOP)
            )
        }

        val content: View? = decor.findViewById(android.R.id.content)
        if (content != null) {
            ViewCompat.setOnApplyWindowInsetsListener(content) { v, windowInsets ->
                val bars = windowInsets.getInsets(SYSTEM_BARS)
                v.setPadding(bars.left, bars.top, bars.right, 0)
                windowInsets
            }
            ViewCompat.requestApplyInsets(content)
        }

        ViewCompat.setOnApplyWindowInsetsListener(scrim) { v, windowInsets ->
            val bars = windowInsets.getInsets(SYSTEM_BARS)
            val params = v.layoutParams
            if (params.height != bars.top) {
                params.height = bars.top
                v.layoutParams = params
            }
            windowInsets
        }

        ViewCompat.requestApplyInsets(scrim)
    }
}
