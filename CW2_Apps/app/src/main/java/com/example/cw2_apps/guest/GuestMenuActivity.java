package com.example.cw2_apps.guest;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cw2_apps.R;
import com.example.cw2_apps.data.repo.MenuRepository;
import com.example.cw2_apps.staff.model.MenuItem;
import com.example.cw2_apps.staff.ui.MenuAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class GuestMenuActivity extends AppCompatActivity {
    private Executor exec = Executors.newSingleThreadExecutor();
    private MenuRepository repo;
    private MenuAdapter adapter;
    private String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_menu);
        repo = new MenuRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, GuestHomeActivity.class);
            startActivity(intent);
            finish();
        });
        androidx.recyclerview.widget.RecyclerView rv = findViewById(R.id.rvMenu);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter(MenuAdapter.Role.GUEST);
        rv.setAdapter(adapter);

        EditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) { }
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) { }
            @Override public void afterTextChanged(Editable s) {
                currentKeyword = s == null ? "" : s.toString();
                loadData(currentKeyword);
            }
        });

        loadData("");
    }

    private void loadData(String keyword) {
        exec.execute(() -> {
            List<MenuItem> data = repo.search(keyword);
            runOnUiThread(() -> adapter.submitList(data));
        });
    }
}
