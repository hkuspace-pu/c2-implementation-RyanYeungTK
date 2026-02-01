package com.example.cw2_apps.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.cw2_apps.data.local.db.ReservationsDbHelper;

public class ReservationRepository {
    private final ReservationsDbHelper dbh;
    public ReservationRepository(Context ctx) { this.dbh = new ReservationsDbHelper(ctx); }

    public long create(String guestUsername, long dateTime, int partySize,  String location, String note) {
        long now = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put("guest_username", guestUsername);
        cv.put("date_time", dateTime);
        cv.put("party_size", partySize);
        cv.put("location", location);
        cv.put("status", "PENDING");
        cv.put("note", note);
        cv.put("created_at", now);
        cv.put("updated_at", now);
        return dbh.getWritableDatabase().insert("reservations", null, cv);
    }

    public Cursor listByUser(String guestUsername) {
        return dbh.getReadableDatabase().query(
                "reservations", null, "guest_username=?",
                new String[]{ guestUsername }, null, null, "date_time DESC");
    }

    public int updateTime(long id, long newDatetime){
        ContentValues cv = new ContentValues();
        cv.put("date_time", newDatetime);
        cv.put("updated_at", System.currentTimeMillis());
        return dbh.getWritableDatabase().update("reservations", cv, "id=?", new String[]{ String.valueOf(id) });
    }
    public int updateStatus(long id, String status) {
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        cv.put("updated_at", System.currentTimeMillis());
        return dbh.getWritableDatabase().update("reservations", cv, "id=?", new String[]{ String.valueOf(id) });
    }

    public int delete(long id) {
        return dbh.getWritableDatabase().delete("reservations", "id=?", new String[]{ String.valueOf(id) });
    }

}
