package com.example.cw2_apps.staff;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.R;
import com.example.cw2_apps.guest.GuestHomeActivity;
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
