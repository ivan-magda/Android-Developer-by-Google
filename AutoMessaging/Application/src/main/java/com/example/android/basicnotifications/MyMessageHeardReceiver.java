package com.example.android.basicnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyMessageHeardReceiver extends BroadcastReceiver {

    private static final String TAG = "MyMessageHeardReceiver";

    public static final String ACTION = "com.example.android.basicnotifications.MY_ACTION_MESSAGE_HEARD";

    public MyMessageHeardReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        final int conversationId = intent.getIntExtra(Extra.CONVERSATION_ID_KEY, -1);
        Log.d(TAG, "MyMessageHeardReceiver for conversation with id: " + conversationId);
    }

}
