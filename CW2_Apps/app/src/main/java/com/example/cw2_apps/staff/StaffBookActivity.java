package com.example.cw2_apps.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.cw2_apps.R;
import com.example.cw2_apps.staff.model.Reservation;
import com.example.cw2_apps.staff.ui.BookingAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class StaffBookActivity extends AppCompatActivity {

    private BookingAdapter adapter;

    private final java.util.concurrent.Executor exec = java.util.concurrent.Executors.newSingleThreadExecutor();
    private com.example.cw2_apps.data.repo.ReservationRepository repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_booking);
        repo = new com.example.cw2_apps.data.repo.ReservationRepository(this);
        loadDataFromDb();

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            startActivity(intent);
            finish();
        });

        RecyclerView rv = findViewById(R.id.rvBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(BookingAdapter.Role.STAFF);
        rv.setAdapter(adapter);

        adapter.setOnItemActionListener(new BookingAdapter.OnItemActionListener() {
            @Override
            public void onConfirm(Reservation r, int position, View sourceView) {
                if (!"PENDING".equals(r.status)) {
                    Snackbar.make(sourceView, "Only pending bookings can be confirmed.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String msg = "Confirm this booking?\n" + r.datetime + " · " + r.party;
                new AlertDialog.Builder(StaffBookActivity.this)
                        .setTitle("Confirm booking")
                        .setMessage(msg)
                        .setPositiveButton("Confirm", (d, w) -> {
                            exec.execute(() -> {
                                int rows = repo.updateStatus(r.id, "CONFIRMED");
                                runOnUiThread(() -> {
                                    if (rows > 0) {
                                        adapter.updateItemStatus(position, "CONFIRMED");
                                        Snackbar.make(sourceView, "Booking confirmed", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(sourceView, "Update failed", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
            @Override
            public void onDelete(Reservation r, int position, View sourceView) {
                if (!"CANCELLED".equals(r.status)) {
                    Snackbar.make(sourceView, "Only cancelled bookings can be deleted.", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(StaffBookActivity.this)
                        .setTitle("Delete booking")
                        .setMessage("This will permanently remove the record.\n" + r.datetime + " · " + r.party)
                        .setPositiveButton("Delete", (d, w) -> {
                            exec.execute(() -> {
                                int rows = repo.delete(r.id);
                                runOnUiThread(() -> {
                                    if (rows > 0) {
                                        adapter.removeItem(position);
                                        Snackbar.make(sourceView, "Booking deleted", Snackbar.LENGTH_SHORT).show();
                                    } else {
                                        Snackbar.make(sourceView, "Delete failed", Snackbar.LENGTH_SHORT).show();
                                    }
                                });
                            });
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });
    }
    private void loadDataFromDb() {
        exec.execute(() -> {
            java.util.List<com.example.cw2_apps.staff.model.Reservation> list = new java.util.ArrayList<>();
            com.example.cw2_apps.data.local.db.ReservationsDbHelper dbh =
                    new com.example.cw2_apps.data.local.db.ReservationsDbHelper(this);
            android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();

            String[] cols = new String[]{"id","date_time","party_size","location","status","guest_username"};
            android.database.Cursor c = db.query("reservations", cols, null, null, null, null, "date_time DESC");
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US);
            while (c.moveToNext()) {
                long id = c.getLong(0);
                long when = c.getLong(1);
                int partySize = c.getInt(2);
                String location = c.getString(3);
                String status = c.getString(4);
                String guest = c.getString(5);
                String dt = fmt.format(new java.util.Date(when));
                String partyText = partySize + " People - " + guest + " · " + location;
                list.add(new com.example.cw2_apps.staff.model.Reservation(id, dt, partyText, status));
            }
            c.close();
            db.close();

            runOnUiThread(() -> adapter.submitList(list));
        });
    }
}