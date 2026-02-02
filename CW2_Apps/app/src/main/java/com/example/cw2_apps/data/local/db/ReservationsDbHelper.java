package com.example.cw2_apps.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReservationsDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app_local.db";
    private static final int DB_VER = 2;

    public ReservationsDbHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VER); }


    private static final String SQL_CREATE_RESERVATIONS =
            "CREATE TABLE IF NOT EXISTS reservations (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "guest_username TEXT," +
                    "date_time INTEGER," +
                    "party_size INTEGER," +
                    "location TEXT," +
                    "status TEXT," +      // PENDING / CONFIRMED / CANCELLED
                    "note TEXT," +
                    "created_at INTEGER," +
                    "updated_at INTEGER)";


    private static final String SQL_CREATE_MENU =
            "CREATE TABLE IF NOT EXISTS menu_items (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT NOT NULL," +
                    "price REAL NOT NULL," +
                    "image_uri TEXT," +
                    "created_at INTEGER," +
                    "updated_at INTEGER)";


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_RESERVATIONS);
        db.execSQL(SQL_CREATE_MENU);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        if (oldV < 2) {
            db.execSQL(SQL_CREATE_MENU);
        }

    }

}
