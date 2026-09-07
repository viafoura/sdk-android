package com.viafourasample.src.utils

import android.content.Context
import android.util.DisplayMetrics

object AndroidUtils {
    fun convertDpToPixel(dp: Float, context: Context): Float =
        dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
