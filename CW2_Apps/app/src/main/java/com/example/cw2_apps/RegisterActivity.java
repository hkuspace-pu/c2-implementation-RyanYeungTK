package com.example.cw2_apps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2_apps.data.repo.AuthRepository;
import com.example.cw2_apps.data.repo.IAuthRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity {
    private TextInputLayout tilAccount, tilPassword, tilConfirmPassword, tilFirstName, tilLastName, tilEmail, tilContact;
    private TextInputEditText etAccount, etPassword, etConfirmPassword, etFirst, etLast, etEmail, etContact;
    private View progress;
    private Button btnRegister;

    private final IAuthRepository repo = new AuthRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        bindViews();
        btnRegister.setOnClickListener(v -> tryRegister());
    }

    private void bindViews() {
        tilAccount = findViewById(R.id.tilAccount);
        tilPassword = findViewById(R.id.tilPassword);
        tilConfirmPassword = findViewById(R.id.tilConfirmPassword);
        tilFirstName = findViewById(R.id.tilFirstName);
        tilLastName = findViewById(R.id.tilLastName);
        tilEmail = findViewById(R.id.tilEmail);
        tilContact = findViewById(R.id.tilContact);
        etAccount = findViewById(R.id.etAccount);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etFirst = findViewById(R.id.etUserFirstName);
        etLast = findViewById(R.id.etUserLastName);
        etEmail = findViewById(R.id.etUserEmail);
        etContact = findViewById(R.id.etUserContact);
        btnRegister = findViewById(R.id.btnRegister);
        progress = findViewById(R.id.progress);
    }

    private void tryRegister() {
        clearErrors();

        String username = safe(etAccount);
        String pass = safe(etPassword);
        String pass2 = safe(etConfirmPassword);
        String first = safe(etFirst);
        String last = safe(etLast);
        String email = safe(etEmail);
        String contact = safe(etContact);

        if (username.isEmpty()) { tilAccount.setError(getString(R.string.required)); return; }
        if (pass.isEmpty()) { tilPassword.setError(getString(R.string.required)); return; }
        if (!pass.equals(pass2)) { tilConfirmPassword.setError(getString(R.string.password_not_match)); return; }
        if (first.isEmpty()) { tilFirstName.setError(getString(R.string.required)); return; }
        if (last.isEmpty()) { tilLastName.setError(getString(R.string.required)); return; }
        if (!email.matches(".+@.+\\..+")) { tilEmail.setError(getString(R.string.invalid_email)); return; }
        if (!contact.matches("\\d{6,}")) { tilContact.setError(getString(R.string.invalid_contact)); return; }

        setLoading(true);
        repo.register(this, "20337839", username, pass, first, last, email, contact, "guest", new IAuthRepository.RegisterCallback() {
                    @Override public void onSuccess() {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this, R.string.register_ok, Toast.LENGTH_SHORT).show();
                        Intent it = new Intent(RegisterActivity.this, LoginActivity.class);
                        it.putExtra("prefill_username", username);
                        startActivity(it);
                        finish();
                    }
                    @Override public void onError(String message) {
                        setLoading(false);
                        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void clearErrors() {
        tilAccount.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);
        tilFirstName.setError(null);
        tilLastName.setError(null);
        tilEmail.setError(null);
        tilContact.setError(null);
    }

    private void setLoading(boolean on) {
        progress.setVisibility(on ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!on);
    }

    private static String safe(TextInputEditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }
}
