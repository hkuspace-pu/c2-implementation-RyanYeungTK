package com.example.cw2_apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2_apps.guest.GuestHomeActivity;
import com.example.cw2_apps.staff.StaffHomeActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;


public class LoginActivity extends AppCompatActivity {

    private ChipGroup chipGroupRole;
    private Chip chipGuest, chipStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        chipGroupRole = findViewById(R.id.chipGroupRole);
        chipGuest = findViewById(R.id.chipGuest);
        chipStaff = findViewById(R.id.chipStaff);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(v -> {

            int checkedId = chipGroupRole.getCheckedChipId();
            if (checkedId == View.NO_ID) {
                Toast.makeText(this, "Please choose a role", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent;
            if (checkedId == R.id.chipGuest) {
                intent = new Intent(this, GuestHomeActivity.class);
            } else {
                intent = new Intent(this, StaffHomeActivity.class);
            }

            // TODO: optionally validate account/password here before navigating

            startActivity(intent);
            finish();

        });


        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

}
