package com.example.cw2_apps.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.cw2_apps.R;
public class NotificationHelper {
    public static final String CH_ID = "reservation_channel";

    public static void ensureChannel(Context ctx){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm.getNotificationChannel(CH_ID) == null) {
                NotificationChannel ch = new NotificationChannel(CH_ID,
                        "Reservations", NotificationManager.IMPORTANCE_HIGH);
                ch.setDescription("Reservation reminders and updates");
                ch.enableVibration(true);
                ch.enableLights(true);
                ch.setLightColor(Color.BLUE);
                nm.createNotificationChannel(ch);
            }
        }
    }

    public static NotificationCompat.Builder builder(Context ctx, String title, String text){
        return new NotificationCompat.Builder(ctx, CH_ID)
                .setSmallIcon(R.drawable.ic_notifications_24)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
    }
}
