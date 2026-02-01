package com.example.cw2_apps.guest;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.LoginActivity;
import com.example.cw2_apps.R;
import com.example.cw2_apps.data.session.SessionStore;
import com.google.android.material.appbar.MaterialToolbar;
import android.widget.TextView;


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

        TextView tvWelcome = findViewById(R.id.tvWelcome);
        String uname = SessionStore.username(this);
        if (tvWelcome != null) {
            String username= uname != null ? uname : "Guest";
            tvWelcome.setText("Welcome! " + username);
        }


        btnMenu.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestMenuActivity.class);
            startActivity(intent);
        });

        btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestBookingActivity.class);
            startActivity(intent);
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
