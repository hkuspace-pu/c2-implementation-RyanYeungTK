package com.example.cw2_apps.notifications;


import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class NotificationQueue {
    private static final String PREF = "notice_queue";
    private static final String KEY_CONFIRMED = "confirmed_ids";
    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }

    public static void enqueueConfirmed(Context c, long reservationId) {
        SharedPreferences p = sp(c);
        Set<String> s = new HashSet<>(p.getStringSet(KEY_CONFIRMED, new HashSet<>()));
        s.add(String.valueOf(reservationId));
        p.edit().putStringSet(KEY_CONFIRMED, s).apply();
    }

    public static Set<String> pollAllConfirmed(Context c) {
        SharedPreferences p = sp(c);
        Set<String> s = new HashSet<>(p.getStringSet(KEY_CONFIRMED, new HashSet<>()));
        p.edit().putStringSet(KEY_CONFIRMED, new HashSet<>()).apply();
        return s;
    }

}
