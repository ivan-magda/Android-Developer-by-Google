package com.example.android.sunshine.sync;

import android.app.IntentService;
import android.content.Intent;

public final class SunshineSyncIntentService extends IntentService {

    public SunshineSyncIntentService() {
        super(SunshineSyncIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SunshineSyncTask.syncWeather(this);
    }

}
