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
import java.util.ArrayList;
import java.util.List;

public class StaffBookActivity extends AppCompatActivity {

    private BookingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_booking);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            startActivity(intent);
            finish();
        });

        // RecyclerView
        RecyclerView rv = findViewById(R.id.rvBookings);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BookingAdapter(BookingAdapter.Role.STAFF);
        rv.setAdapter(adapter);

        adapter.submitList(mockData());

        adapter.setOnItemActionListener(new BookingAdapter.OnItemActionListener() {
            @Override public void onConfirm(Reservation r, int position, View sourceView) {
                String msg = "Confirm this booking?\n" + r.datetime + " · " + r.party;
                new AlertDialog.Builder(StaffBookActivity.this)
                        .setTitle("Confirm booking")
                        .setMessage(msg)
                        .setPositiveButton("Confirm", (d,w)->{
                            adapter.updateItemStatus(position, "CONFIRMED");
                            Snackbar.make(sourceView, "Booking confirmed", Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            @Override public void onCancel(Reservation r, int position, View sourceView) {
                new AlertDialog.Builder(StaffBookActivity.this)
                        .setTitle("Cancel booking")
                        .setMessage("Are you sure to cancel this booking?")
                        .setPositiveButton("Yes", (d,w)->{
                            adapter.updateItemStatus(position, "CANCELLED");
                            Snackbar.make(sourceView, "Booking cancelled", Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private List<Reservation> mockData() {
        List<Reservation> l = new ArrayList<>();
        l.add(new Reservation(1, "2026/02/01 19:00", "2 People - John", "NEW"));
        l.add(new Reservation(2, "2026/02/01 20:00", "3 People - Tina", "CONFIRMED"));
        l.add(new Reservation(3, "2026/02/02 18:30", "4 People - Alex", "NEW"));
        return l;
    }
}