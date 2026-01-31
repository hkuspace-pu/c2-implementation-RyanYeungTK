package com.example.cw2_apps.staff;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.LoginActivity;
import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;

public class StaffHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_home);

        Button btnMenu = findViewById(R.id.btnMenu);
        Button btnBooking = findViewById(R.id.btnBooking);
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
            Intent intent = new Intent(this, StaffMenuActivity.class);
            startActivity(intent);
            finish();
        });

        btnBooking.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffBookActivity.class);
            startActivity(intent);
            finish();
        });


        btnNotice.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffNoticeActivity.class);
            startActivity(intent);
        });

        btnSetting.setOnClickListener(v -> {
            Intent intent = new Intent(this, StaffSettingActivity.class);
            startActivity(intent);
        });
    }
}
