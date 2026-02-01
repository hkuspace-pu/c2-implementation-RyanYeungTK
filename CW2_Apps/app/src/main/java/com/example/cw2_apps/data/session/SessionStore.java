package com.example.cw2_apps.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionStore {

    private static final String PREF = "app_session";
    private static final String K_USER = "username";
    private static final String K_ROLE = "role";

    public static void save(Context ctx, String username, String role) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(K_USER, username).putString(K_ROLE, role).apply();
    }

    public static void clear(Context ctx) {
        ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit().clear().apply();
    }

    public static String role(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(K_ROLE, null);
    }

    public static String username(Context ctx) {
        return ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(K_USER, null);
    }

}
