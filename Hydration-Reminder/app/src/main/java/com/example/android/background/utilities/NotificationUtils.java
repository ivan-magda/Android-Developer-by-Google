package com.example.android.background.utilities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.background.MainActivity;
import com.example.android.background.R;
import com.example.android.background.sync.ReminderTasks;
import com.example.android.background.sync.WaterReminderIntentService;

/**
 * Utility class for creating hydration notifications
 */
public final class NotificationUtils {

    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    private static final int WATER_REMINDER_PENDING_INTENT_ID = 100;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 101;
    private static final int ACTION_DRINK_PENDING_INTENT_ID = 102;

    /**
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    private static final int WATER_REMINDER_NOTIFICATION_ID = 200;

    private NotificationUtils() {
    }

    public static void clearAllNotifications(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancelAll();
    }

    // Create a method called remindUserBecauseCharging which takes a Context.
    // This method will create a notification for charging. It might be helpful
    // to take a look at this guide to see an example of what the code in this method will look like:
    // https://developer.android.com/training/notify-user/build-notification.html
    public static void remindUserBecauseCharging(Context context) {
        // In the remindUser method use NotificationCompat.Builder to create a notification
        // that:
        // - has a color of R.colorPrimary - use ContextCompat.getColor to get a compatible color
        // - has ic_drink_notification as the small icon
        // - uses icon returned by the largeIcon helper method as the large icon
        // - sets the title to the charging_reminder_notification_title String resource
        // - sets the text to the charging_reminder_notification_body String resource
        // - sets the style to NotificationCompat.BigTextStyle().bigText(text)
        // - sets the notification defaults to vibrate
        // - uses the content intent returned by the contentIntent helper method for the contentIntent
        // - automatically cancels the notification when the notification is clicked
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentText(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(contentIntent(context))
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            builder.setPriority(Notification.PRIORITY_HIGH);
        }

        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(WATER_REMINDER_NOTIFICATION_ID, builder.build());
    }

    // Create a helper method called largeIcon which takes in a Context as a parameter and
    // returns a Bitmap. This method is necessary to decode a bitmap needed for the notification.
    private static Bitmap largeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_local_drink_black_24px);
    }

    // Create a helper method called contentIntent with a single parameter for a Context. It
    // should return a PendingIntent. This method will create the pending intent which will trigger when
    // the notification is pressed. This pending intent should open up the MainActivity.
    private static PendingIntent contentIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        // Create a PendingIntent using getActivity that:
        // - Take the context passed in as a parameter
        // - Takes an unique integer ID for the pending intent (you can create a constant for
        //   this integer above
        // - Takes the intent to open the MainActivity you just created; this is what is triggered
        //   when the notification is triggered
        // - Has the flag FLAG_UPDATE_CURRENT, so that if the intent is created again, keep the
        // intent but update the data
        return PendingIntent.getActivity(
                context,
                WATER_REMINDER_PENDING_INTENT_ID,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );
    }

    private static NotificationCompat.Action ignoreReminderAction(Context context) {
        Intent intent = new Intent(context, WaterReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION);
        PendingIntent pendingIntent = PendingIntent.getService(context, ACTION_IGNORE_PENDING_INTENT_ID,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action(R.drawable.ic_cancel_black_24px, "No, thanks.", pendingIntent);
    }

    private static NotificationCompat.Action drinkWaterAction(Context context) {
        Intent intent = new Intent(context, WaterReminderIntentService.class);
        intent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT);
        PendingIntent pendingIntent = PendingIntent.getService(context, ACTION_DRINK_PENDING_INTENT_ID,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        return new NotificationCompat.Action(R.drawable.ic_local_drink_black_24px, "I did it!", pendingIntent);
    }

}
