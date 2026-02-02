package com.example.cw2_apps.data.session;

import android.content.Context;
import android.content.SharedPreferences;

public final class ProfileStore {
    private static final String PREF = "profile_store";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_CONTACT = "contact";

    private static SharedPreferences sp(Context c) {
        return c.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    public static void save(Context c, String name, String email, String contact) {
        sp(c).edit()
                .putString(KEY_NAME, safe(name))
                .putString(KEY_EMAIL, safe(email))
                .putString(KEY_CONTACT, safe(contact))
                .apply();
    }

    public static String name(Context c)    { return sp(c).getString(KEY_NAME, ""); }
    public static String email(Context c)   { return sp(c).getString(KEY_EMAIL, ""); }
    public static String contact(Context c) { return sp(c).getString(KEY_CONTACT, ""); }

    private static String safe(String s) { return s == null ? "" : s.trim(); }

    private ProfileStore() {}
}
