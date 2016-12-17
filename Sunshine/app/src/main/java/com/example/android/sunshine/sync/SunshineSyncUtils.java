package com.example.android.sunshine.sync;

import android.content.Context;
import android.content.Intent;

public final class SunshineSyncUtils {

    private SunshineSyncUtils() {
    }

    public static void startImmediateSync(Context context) {
        context.startService(new Intent(context, SunshineSyncIntentService.class));
    }

}
