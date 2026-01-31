package com.example.cw2_apps.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2_apps.LoginActivity;
import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;

public class GuestReservationActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_make_reservation);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, GuestHomeActivity.class);
            startActivity(intent);
            finish();
        });

        Spinner spinner = findViewById(R.id.spinnerLocation);
        String[] items = new String[]{"Sha Tin", "Kai Tak", "Mong Kok"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(adapter);

    }
}
