package com.lifesaver.blooddonation.managers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.lifesaver.blooddonation.R;
import com.lifesaver.blooddonation.activities.MainActivity;
import com.lifesaver.blooddonation.constants.AppConstants;

public final class NotificationHelper {
    private NotificationHelper() {}

    public static void ensureChannel(Context ctx) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;
        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm == null) return;
        if (nm.getNotificationChannel(AppConstants.NOTIFICATION_CHANNEL_ID) != null) return;
        NotificationChannel ch = new NotificationChannel(
                AppConstants.NOTIFICATION_CHANNEL_ID,
                ctx.getString(R.string.default_notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH);
        ch.setDescription("Blood camp & request alerts");
        nm.createNotificationChannel(ch);
    }

    public static void show(Context ctx, int id, String title, String body) {
        ensureChannel(ctx);
        Intent open = new Intent(ctx, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int piFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            piFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pi = PendingIntent.getActivity(ctx, 0, open, piFlags);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx,
                AppConstants.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_blood_drop)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pi);

        NotificationManager nm = ctx.getSystemService(NotificationManager.class);
        if (nm != null) nm.notify(id, b.build());
    }
}
