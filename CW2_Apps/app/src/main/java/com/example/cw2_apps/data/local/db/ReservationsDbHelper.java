package com.example.cw2_apps.data.local.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ReservationsDbHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "app_local.db";
    private static final int DB_VER = 1;

    public ReservationsDbHelper(Context ctx) { super(ctx, DB_NAME, null, DB_VER); }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE reservations (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "guest_username TEXT," +
                "date_time INTEGER," +
                "party_size INTEGER," +
                "location TEXT," +
                "status TEXT," +    // PENDING / CONFIRMED / CANCELLED
                "note TEXT," +
                "created_at INTEGER," +
                "updated_at INTEGER)");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS reservations");
        onCreate(db);
    }

}
