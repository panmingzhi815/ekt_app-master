package com.hbtl.utils;

import android.content.Context;

public class DimenUtils {

    public static int dp2px(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat
                * paramContext.getResources().getDisplayMetrics().density);
    }

    public static int px2dp(Context paramContext, float paramFloat) {
        return (int) (0.5F + paramFloat
                / paramContext.getResources().getDisplayMetrics().density);
    }
}
