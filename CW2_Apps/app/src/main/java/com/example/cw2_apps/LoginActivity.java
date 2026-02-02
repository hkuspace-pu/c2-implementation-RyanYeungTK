package com.example.cw2_apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.data.repo.AuthRepository;
import com.example.cw2_apps.data.repo.IAuthRepository;
import com.example.cw2_apps.data.session.SessionStore;
import com.example.cw2_apps.domain.model.AuthSession;
import com.example.cw2_apps.domain.model.Role;
import com.example.cw2_apps.guest.GuestHomeActivity;
import com.example.cw2_apps.staff.StaffHomeActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText etAccount, etPassword;
    private static final String PREFILL_USERNAME = "prefill_username";
    private View progress;
    private IAuthRepository authRepo = new AuthRepository();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        progress  = findViewById(R.id.progress);
        AuthRepository authRepo = new AuthRepository();
        prefillUsernameFromIntent(getIntent());

        btnLogin.setOnClickListener(v -> {
            String u = etAccount.getText().toString().trim();
            String p = etPassword.getText().toString();
            if (u.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Please enter your account and password", Toast.LENGTH_SHORT).show();
                return;
            }
            progress.setVisibility(View.VISIBLE);
            btnLogin.setEnabled(false);
            authRepo.login(this, "20337839", u, p, new IAuthRepository.LoginCallback() {
                        @Override public void onSuccess(AuthSession session) {
                            progress.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            SessionStore.save(LoginActivity.this, session.username, session.role.name().toLowerCase());
                            if (session.role == Role.STAFF) {
                                startActivity(new Intent(LoginActivity.this, StaffHomeActivity.class));
                                finish();
                            } else { // GUEST
                                flushConfirmedNotificationsIfAny(session.username);
                                startActivity(new Intent(LoginActivity.this, GuestHomeActivity.class));
                                finish();
                            }
                        }
                        @Override public void onError(String message) {
                            progress.setVisibility(View.GONE);
                            btnLogin.setEnabled(true);
                            Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        prefillUsernameFromIntent(intent);
    }

    private void prefillUsernameFromIntent(Intent intent) {
        if (intent == null) return;
        String prefill = intent.getStringExtra(PREFILL_USERNAME);
        if (prefill == null || prefill.isEmpty()) return;

        EditText etAccount = findViewById(R.id.etAccount);
        if (etAccount != null) {
            etAccount.setText(prefill);
            etAccount.setSelection(prefill.length());

            EditText etPassword = findViewById(R.id.etPassword);
            if (etPassword != null) {
                etPassword.requestFocus();
            }
        }

    }

    private void flushConfirmedNotificationsIfAny(String username) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        java.util.Set<String> ids =
                com.example.cw2_apps.notifications.NotificationQueue.pollAllConfirmed(this);
        if (ids.isEmpty()) return;

        com.example.cw2_apps.data.local.db.ReservationsDbHelper dbh =
                new com.example.cw2_apps.data.local.db.ReservationsDbHelper(this);
        android.database.sqlite.SQLiteDatabase db = dbh.getReadableDatabase();

        com.example.cw2_apps.notifications.NotificationHelper.ensureChannel(this);

        for (String idStr : ids) {
            long id;
            try { id = Long.parseLong(idStr); } catch (Exception e) { continue; }

            android.database.Cursor c = db.query(
                    "reservations",
                    new String[]{"date_time","location","status","guest_username"},
                    "id=?",
                    new String[]{ String.valueOf(id) },
                    null, null, null
            );
            if (c.moveToFirst()) {
                long when = c.getLong(0);
                String location = c.getString(1);
                String status = c.getString(2);
                String guest = c.getString(3);

                if ("CONFIRMED".equals(status) && username.equals(guest)) {
                    java.text.SimpleDateFormat fmt =
                            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.US);
                    String text = "Your booking at " + location + " on " +
                            fmt.format(new java.util.Date(when)) + " is confirmed";

                    androidx.core.app.NotificationCompat.Builder b =
                            com.example.cw2_apps.notifications.NotificationHelper
                                    .builder(this, getString(R.string.app_name), text);

                    int notifId = (int) (10000 + id);
                    androidx.core.app.NotificationManagerCompat.from(this).notify(notifId, b.build());
                }
            }
            c.close();
        }
        db.close();
    }

}
