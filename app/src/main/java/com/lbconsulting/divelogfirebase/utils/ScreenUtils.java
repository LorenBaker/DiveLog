package com.lbconsulting.divelogfirebase.utils;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * ScreenUtils
 *
 * Conversion between dp and px
 */

public class ScreenUtils {
    private ScreenUtils() {
        throw new AssertionError();
    }

    public static float dpToPx(@NonNull Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static float pxToDp(@NonNull Context context, float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int dpToPx(@NonNull Context context, int dp) {
        return Math.round(dpToPx(context, (float) dp));
    }

    public static int pxToDp(@NonNull Context context, int px) {
        return Math.round(pxToDp(context, (float) px));
    }
}
