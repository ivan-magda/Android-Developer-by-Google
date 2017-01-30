package com.ivanmagda.android.sunshine;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

public final class SunshineColorUtils {

    private SunshineColorUtils() {
    }

    public static int getColor(@NonNull final Context context, final int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getResources().getColor(id, null);
        } else {
            return context.getResources().getColor(id);
        }
    }

    public static int getPrimaryColor(@NonNull final Context context) {
        return getColor(context, R.color.colorPrimary);
    }

    public static int getPrimaryDarkColor(@NonNull final Context context) {
        return getColor(context, R.color.colorPrimaryDark);
    }

    public static int getAccentColor(@NonNull final Context context) {
        return getColor(context, R.color.colorAccent);
    }

    public static int getPrimaryLightColor(@NonNull final Context context) {
        return getColor(context, R.color.colorPrimaryLight);
    }

}
