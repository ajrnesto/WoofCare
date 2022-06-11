package com.woofcare.Notifications;

import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.woofcare.R;

public class BroadcastReceiver extends android.content.BroadcastReceiver {
    public BroadcastReceiver () {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String details = intent.getStringExtra("details");

        Intent intent2 = new Intent(context, IntentService.class);
        context.startService(intent2);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "woofcare_notifications")
                .setSmallIcon(R.drawable.alarm_clock_24)
                .setContentTitle(title)
                .setContentText(details)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(0, builder.build());
    }
}
