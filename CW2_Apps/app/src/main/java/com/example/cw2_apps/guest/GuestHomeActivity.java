package com.example.cw2_apps.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.LoginActivity;
import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;


public class GuestHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_home);

        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnBooking = findViewById(R.id.btnBooking);
        Button btnReservation = findViewById(R.id.btnReservation);
        Button btnNotice = findViewById(R.id.btnNotice);
        Button btnSetting = findViewById(R.id.btnSetting);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_logout) {
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            }
            return false;
        });


        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestMenuActivity.class);
            startActivity(intent);
            finish();
        });

        btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestBookingActivity.class);
            startActivity(intent);
            finish();
        });

        btnReservation.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestReservationActivity.class);
            startActivity(intent);
        });

        btnNotice.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestNoticeActivity.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestSettingActivity.class);
            startActivity(intent);
        });
    }
}
