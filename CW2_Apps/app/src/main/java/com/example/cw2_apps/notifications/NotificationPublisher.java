package com.example.cw2_apps.notifications;

import android.Manifest;
import android.app.NotificationChannel;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.cw2_apps.R;

public class NotificationPublisher extends BroadcastReceiver {
    private static final String CH_ID = "reservation_channel";

    @Override
    public void onReceive(Context ctx, Intent intent) {
        String title = intent.getStringExtra("title");
        String text  = intent.getStringExtra("text");
        int notifId  = intent.getIntExtra("notifId", 0);

        ensureChannel(ctx);
        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx, CH_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title != null ? title : ctx.getString(R.string.app_name))
                .setContentText(text != null ? text : "")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            try {
                NotificationManagerCompat.from(ctx).notify(notifId, b.build());
            } catch (SecurityException ignored) {
            }
        }


    }

    private static void ensureChannel(Context ctx){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm.getNotificationChannel(CH_ID) == null) {
                NotificationChannel ch = new NotificationChannel(CH_ID, "Reservations",
                        NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("Reservation reminders and updates");
                ch.enableVibration(true);
                ch.enableLights(true);
                ch.setLightColor(Color.BLUE);
                nm.createNotificationChannel(ch);
            }
        }
    }

}
