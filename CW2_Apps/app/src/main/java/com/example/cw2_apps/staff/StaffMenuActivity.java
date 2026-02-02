package com.example.cw2_apps.staff;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.cw2_apps.R;
import com.example.cw2_apps.data.repo.MenuRepository;
import com.example.cw2_apps.databinding.DialogMenuEditBinding;
import com.example.cw2_apps.databinding.StaffMenuBinding;
import com.example.cw2_apps.staff.model.MenuItem;
import com.example.cw2_apps.staff.ui.MenuAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


public class StaffMenuActivity extends AppCompatActivity {
    private StaffMenuBinding vb;
    private final Executor exec = Executors.newSingleThreadExecutor();
    private MenuRepository repo;
    private MenuAdapter adapter;
    private volatile String currentKeyword = "";
    private String pickedImageUriTemp;
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) pickedImageUriTemp = uri.toString();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vb = StaffMenuBinding.inflate(getLayoutInflater());
        setContentView(vb.getRoot());

        repo = new MenuRepository(this);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            startActivity(new Intent(this, StaffHomeActivity.class));
            finish();
        });

        vb.rvMenu.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MenuAdapter();
        vb.rvMenu.setAdapter(adapter);

        adapter.setOnItemActionListener(new MenuAdapter.OnItemActionListener() {
            @Override public void onEdit(MenuItem item, int position, View v) {
                openEditDialog(item);
            }
            @Override public void onDelete(MenuItem item, int position, View v) {
                confirmDelete(item, position);
            }
        });

        vb.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { }
            @Override public void afterTextChanged(Editable s) {
                currentKeyword = s == null ? "" : s.toString();
                loadData(currentKeyword);
            }
        });

        vb.fabAdd.setOnClickListener(v -> openAddDialog());
        loadData("");
    }

    private void loadData(String keyword) {
        exec.execute(() -> {
            List<MenuItem> data = repo.search(keyword);
            runOnUiThread(() -> adapter.submitList(data));
        });
    }

    private void openAddDialog() {
        DialogMenuEditBinding form = DialogMenuEditBinding.inflate(LayoutInflater.from(this));
        pickedImageUriTemp = null;
        form.btnPickImage.setOnClickListener(v -> showImageSourceChooser());

        new AlertDialog.Builder(this)
                .setTitle("Add Menu Item")
                .setView(form.getRoot())
                .setPositiveButton("Save", (d, w) -> {
                    String n = safeText(form.etName);
                    String ps = safeText(form.etPrice);
                    if (n.isEmpty() || ps.isEmpty()) {
                        Toast.makeText(this, "Name/Price required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price;
                    try { price = Double.parseDouble(ps); }
                    catch (Exception e) { Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show(); return; }

                    final String image = pickedImageUriTemp;
                    exec.execute(() -> {
                        repo.insert(n, price, image);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
                            loadData(currentKeyword);
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openEditDialog(MenuItem item) {
        DialogMenuEditBinding form = DialogMenuEditBinding.inflate(LayoutInflater.from(this));
        pickedImageUriTemp = item.imageUri;
        form.btnPickImage.setOnClickListener(v -> showImageSourceChooser());

        form.etName.setText(item.name);
        form.etPrice.setText(String.valueOf((long)item.price));
        form.btnPickImage.setOnClickListener(v -> pickImageLauncher.launch("image/*"));

        new AlertDialog.Builder(this)
                .setTitle("Edit Menu Item")
                .setView(form.getRoot())
                .setPositiveButton("Save", (d, w) -> {
                    String n = safeText(form.etName);
                    String ps = safeText(form.etPrice);
                    if (n.isEmpty() || ps.isEmpty()) {
                        Toast.makeText(this, "Name/Price required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price;
                    try { price = Double.parseDouble(ps); }
                    catch (Exception e) { Toast.makeText(this, "Invalid price", Toast.LENGTH_SHORT).show(); return; }

                    final String image = pickedImageUriTemp;
                    exec.execute(() -> {
                        repo.update(item.id, n, price, image);
                        runOnUiThread(() -> {
                            Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                            loadData(currentKeyword);
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(MenuItem item, int position) {
        new AlertDialog.Builder(this)
                .setMessage("Delete this item?")
                .setPositiveButton("Delete", (d, w) -> {
                    exec.execute(() -> {
                        int rows = repo.delete(item.id);
                        runOnUiThread(() -> {
                            if (rows > 0) {
                                adapter.removeAt(position);
                                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(this, "Delete failed", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private static String safeText(EditText et) {
        return et.getText() == null ? "" : et.getText().toString().trim();
    }


    private void showImageSourceChooser() {
        String[] options = new String[]{"Built-in images", "Gallery"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Choose image")
                .setItems(options, (d, which) -> {
                    if (which == 0) {
                        showBuiltinImagesDialog();
                    } else {
                        // 走相簿
                        pickImageLauncher.launch("image/*");
                    }
                })
                .show();
    }
    private void showBuiltinImagesDialog() {
        final String[] labels = {"Pasta", "Salad", "Pizza"};
        final int[] resIds = { R.drawable.pasta, R.drawable.salad, R.drawable.pizza };

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Built-in images")
                .setItems(labels, (d, idx) -> {
                    String entry = getResources().getResourceEntryName(resIds[idx]);
                    pickedImageUriTemp = "res:" + entry;
                    android.widget.Toast.makeText(this, "Selected " + labels[idx], android.widget.Toast.LENGTH_SHORT).show();
                })
                .show();
    }


}