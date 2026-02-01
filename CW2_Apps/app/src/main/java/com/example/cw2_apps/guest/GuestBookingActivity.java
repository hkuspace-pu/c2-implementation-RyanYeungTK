package com.example.cw2_apps.guest;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.TextView;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cw2_apps.data.repo.ReservationRepository;
import com.example.cw2_apps.data.session.SessionStore;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class GuestBookingActivity extends AppCompatActivity {
    private RecyclerView rv; private TextView tvEmpty;
    private ReservationRepository repo; private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_my_booking);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());
        rv = findViewById(R.id.rvBookings);
        tvEmpty = findViewById(R.id.tvEmpty);
        rv.setLayoutManager(new LinearLayoutManager(this));
        repo = new ReservationRepository(this);
        load();
    }

    private void load(){
        Cursor c = repo.listByUser(SessionStore.username(this));
        if (c == null || c.getCount() == 0) {
            rv.setAdapter(null); tvEmpty.setVisibility(View.VISIBLE);
            if (c != null) c.close();
            return;
        }
        tvEmpty.setVisibility(View.GONE);
        rv.setAdapter(new A(c));
    }
    class A extends RecyclerView.Adapter<VH> {
        Cursor c; A(Cursor c){ this.c = c; }
        @Override public VH onCreateViewHolder(ViewGroup p, int v){
            View view = LayoutInflater.from(p.getContext()).inflate(R.layout.item_reservation, p, false);
            return new VH(view);
        }
        @Override public void onBindViewHolder(VH h, int pos){
            c.moveToPosition(pos);
            long id = c.getLong(c.getColumnIndexOrThrow("id"));
            long when = c.getLong(c.getColumnIndexOrThrow("date_time"));
            int size = c.getInt(c.getColumnIndexOrThrow("party_size"));
            String loc = c.getString(c.getColumnIndexOrThrow("location"));
            String status = c.getString(c.getColumnIndexOrThrow("status"));
            h.tvWhen.setText(sdf.format(when));
            h.tvMeta.setText(getString(R.string.resv_meta, size, loc, status));

            h.btnCancel.setOnClickListener(v -> {
                repo.updateStatus(id, "CANCELLED");
                Toast.makeText(GuestBookingActivity.this, R.string.reservation_cancelled, Toast.LENGTH_SHORT).show();
                load();
            });
            h.btnEdit.setOnClickListener(v -> editTime(id, when));
        }
        @Override public int getItemCount(){ return c.getCount(); }
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvWhen, tvMeta; View btnEdit, btnCancel;
        VH(View v){ super(v); tvWhen=v.findViewById(R.id.tvWhen); tvMeta=v.findViewById(R.id.tvMeta);
            btnEdit=v.findViewById(R.id.btnEdit); btnCancel=v.findViewById(R.id.btnCancel); }
    }

    private void editTime(long id, long when){
        Calendar c = Calendar.getInstance(); c.setTimeInMillis(when);
        new DatePickerDialog(this, (dv, y, m, d) -> {
            c.set(Calendar.YEAR, y); c.set(Calendar.MONTH, m); c.set(Calendar.DAY_OF_MONTH, d);
            new TimePickerDialog(this, (tv, h, min) -> {
                c.set(Calendar.HOUR_OF_DAY, h); c.set(Calendar.MINUTE, min); c.set(Calendar.SECOND, 0);
                repo.updateTime(id, c.getTimeInMillis());
                Toast.makeText(this, R.string.reservation_updated, Toast.LENGTH_SHORT).show();
                load();
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

}
