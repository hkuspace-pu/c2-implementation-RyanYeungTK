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
                            Intent it = (session.role == Role.STAFF)
                                    ? new Intent(LoginActivity.this, StaffHomeActivity.class)
                                    : new Intent(LoginActivity.this, GuestHomeActivity.class);
                            startActivity(it);
                            finish();
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

}
