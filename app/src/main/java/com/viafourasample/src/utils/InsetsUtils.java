package com.viafourasample.src.utils;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.viafoura.sampleapp.R;

public final class InsetsUtils {

    private static final int SYSTEM_BARS =
            WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout();

    private InsetsUtils() {
    }

    public static void enableEdgeToEdge(AppCompatActivity activity) {
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        new WindowInsetsControllerCompat(window, window.getDecorView())
                .setAppearanceLightStatusBars(false);
    }

    public static void applyActionBarInsets(AppCompatActivity activity) {
        enableEdgeToEdge(activity);

        View decor = activity.getWindow().getDecorView();
        if (!(decor instanceof FrameLayout)) {
            return;
        }

        View scrim = decor.findViewById(R.id.status_bar_scrim);
        if (scrim == null) {
            scrim = new View(activity);
            scrim.setId(R.id.status_bar_scrim);
            scrim.setBackgroundColor(ContextCompat.getColor(activity, R.color.colorToolbar));
            ((FrameLayout) decor).addView(scrim, 0, new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 0, Gravity.TOP));
        }

        View content = decor.findViewById(android.R.id.content);
        if (content != null) {
            ViewCompat.setOnApplyWindowInsetsListener(content, new OnApplyWindowInsetsListener() {
                @Override
                public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat windowInsets) {
                    Insets bars = windowInsets.getInsets(SYSTEM_BARS);
                    v.setPadding(bars.left, bars.top, bars.right, 0);
                    return windowInsets;
                }
            });
            ViewCompat.requestApplyInsets(content);
        }

        ViewCompat.setOnApplyWindowInsetsListener(scrim, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat windowInsets) {
                Insets bars = windowInsets.getInsets(SYSTEM_BARS);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                if (params.height != bars.top) {
                    params.height = bars.top;
                    v.setLayoutParams(params);
                }
                return windowInsets;
            }
        });

        ViewCompat.requestApplyInsets(scrim);
    }
}
