package com.example.cw2_apps.data.repo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.cw2_apps.data.local.db.ReservationsDbHelper;
import com.example.cw2_apps.staff.model.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MenuRepository {
    private final ReservationsDbHelper dbh;
    public MenuRepository(Context ctx) { this.dbh = new ReservationsDbHelper(ctx); }

    public long insert(String name, double price, String imageUri) {
        long now = System.currentTimeMillis();
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        cv.put("image_uri", imageUri);
        cv.put("created_at", now);
        cv.put("updated_at", now);
        return dbh.getWritableDatabase().insert("menu_items", null, cv);
    }

    public int update(long id, String name, double price, String imageUri) {
        ContentValues cv = new ContentValues();
        cv.put("name", name);
        cv.put("price", price);
        cv.put("image_uri", imageUri);
        cv.put("updated_at", System.currentTimeMillis());
        return dbh.getWritableDatabase().update("menu_items", cv, "id=?",
                new String[]{ String.valueOf(id) });
    }

    public int delete(long id) {
        return dbh.getWritableDatabase().delete("menu_items", "id=?",
                new String[]{ String.valueOf(id) });
    }
    public List<MenuItem> search(String keyword) {
        List<MenuItem> list = new ArrayList<>();
        String sel = null; String[] args = null;
        if (keyword != null && !keyword.trim().isEmpty()) {
            sel = "name LIKE ?";
            args = new String[]{ "%" + keyword.trim() + "%" };
        }
        Cursor c = dbh.getReadableDatabase().query(
                "menu_items",
                new String[]{"id","name","price","image_uri"},
                sel, args, null, null,
                "updated_at DESC");
        while (c.moveToNext()) {
            long id = c.getLong(0);
            String name = c.getString(1);
            double price = c.getDouble(2);
            String image = c.getString(3);
            list.add(new MenuItem(id, name, price, image));
        }
        c.close();
        return list;
    }
}