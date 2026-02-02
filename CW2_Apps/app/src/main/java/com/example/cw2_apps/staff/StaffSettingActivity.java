package com.example.cw2_apps.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.R;
import com.example.cw2_apps.data.session.ProfileStore;
import com.example.cw2_apps.data.session.SessionStore;
import com.google.android.material.appbar.MaterialToolbar;

public class StaffSettingActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_setting);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            startActivity(intent);
            finish();
        });

        TextView tvAccountName  = findViewById(R.id.tvAccountName);
        TextView tvEmailValue   = findViewById(R.id.tvEmailValue);
        TextView tvContactValue = findViewById(R.id.tvContactValue);
        String name    = ProfileStore.name(this);
        String email   = ProfileStore.email(this);
        String contact = ProfileStore.contact(this);
        if (name == null || name.trim().isEmpty()) {
            String fallback = SessionStore.username(this);
            name = (fallback == null || fallback.isEmpty()) ? "—" : fallback;
        }
        if (email == null || email.trim().isEmpty())   email = "—";
        if (contact == null || contact.trim().isEmpty()) contact = "—";
        tvAccountName.setText(name);
        tvEmailValue.setText(email);
        tvContactValue.setText(contact);

        Spinner spinner = findViewById(R.id.spinnerNotify);

        String[] items = new String[] {
                "All Updates",
                "Booking Updates",
                "Menu Updates",
                "None"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                items
        );
        spinner.setAdapter(adapter);
    }
}
