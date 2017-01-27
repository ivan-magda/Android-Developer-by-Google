package com.example.android.basicnotifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.util.Log;

public class MyMessageReplyReceiver extends BroadcastReceiver {

    private static final String TAG = "MyMessageReplyReceiver";

    public static final String ACTION = "com.example.android.basicnotifications.MY_ACTION_MESSAGE_REPLY";

    public MyMessageReplyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        final int conversationId = intent.getIntExtra(Extra.CONVERSATION_ID_KEY, -1);

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            CharSequence replyText = remoteInput.getCharSequence(Extra.VOICE_REPLY_KEY);
            Log.d(TAG, "Found voice reply [" + replyText + "]");
        }
    }

}
