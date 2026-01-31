package com.example.cw2_apps.staff;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class StaffMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.staff_menu);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, StaffHomeActivity.class);
            startActivity(intent);
            finish();
        });

        FloatingActionButton fabAdd = findViewById(R.id.fabAdd);
        if (fabAdd != null) {
            fabAdd.setOnClickListener(v -> openAddDialog());
        }


         bindDemoCardButtons(R.id.btnEdit_pizza, R.id.btnDelete_pizza, R.id.tvName_pizza, R.id.tvPrice_pizza, 1L);
         bindDemoCardButtons(R.id.btnEdit_pasta, R.id.btnDelete_pasta, R.id.tvName_pasta, R.id.tvPrice_pasta, 2L);
         bindDemoCardButtons(R.id.btnEdit_salad, R.id.btnDelete_salad, R.id.tvName_salad, R.id.tvPrice_salad, 3L);
    }

    private void bindDemoCardButtons(int editBtnId, int deleteBtnId, int nameTvId, int priceTvId, long itemId) {
        MaterialButton btnEdit = findViewById(editBtnId);
        MaterialButton btnDelete = findViewById(deleteBtnId);
        TextView tvName = findViewById(nameTvId);
        TextView tvPrice = findViewById(priceTvId);

        if (btnEdit != null && tvName != null && tvPrice != null) {
            btnEdit.setOnClickListener(v -> openEditDialog(
                    itemId,
                    tvName.getText().toString(),
                    tvPrice.getText().toString()
            ));
        }
        if (btnDelete != null) {
            btnDelete.setOnClickListener(v -> confirmDelete(itemId));
        }
    }

    private void openAddDialog() {
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_menu_edit, null);
        EditText etName = form.findViewById(R.id.etName);
        EditText etPrice = form.findViewById(R.id.etPrice);

        new AlertDialog.Builder(this)
                .setTitle("Add Menu Item")
                .setView(form)
                .setPositiveButton("Save", (d, w) -> {
                    String n = etName.getText().toString().trim();
                    String ps = etPrice.getText().toString().trim();
                    if (n.isEmpty() || ps.isEmpty()) {
                        Toast.makeText(this, "Name/Price required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price = Double.parseDouble(ps);

                    // TODO: 之後接你的 Repository / SQLite
                    insertMenu(n, price, /*imageUri*/ null);

                    Toast.makeText(this, "Saved (demo)", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void openEditDialog(long itemId, String currentName, String currentPrice) {
        View form = LayoutInflater.from(this).inflate(R.layout.dialog_menu_edit, null);
        EditText etName = form.findViewById(R.id.etName);
        EditText etPrice = form.findViewById(R.id.etPrice);

        etName.setText(currentName);
        etPrice.setText(currentPrice);

        new AlertDialog.Builder(this)
                .setTitle("Edit Menu Item")
                .setView(form)
                .setPositiveButton("Save", (d, w) -> {
                    String n = etName.getText().toString().trim();
                    String ps = etPrice.getText().toString().trim();
                    if (n.isEmpty() || ps.isEmpty()) {
                        Toast.makeText(this, "Name/Price required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double price = Double.parseDouble(ps);

                    // TODO: 之後接你的 Repository / SQLite
                    updateMenu(itemId, n, price, /*imageUri*/ null);

                    Toast.makeText(this, "Updated (demo)", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void confirmDelete(long itemId) {
        new AlertDialog.Builder(this)
                .setMessage("Delete this item?")
                .setPositiveButton("Delete", (d, w) -> {
                    // TODO: 之後接你的 Repository / SQLite
                    deleteMenu(itemId);

                    Toast.makeText(this, "Deleted (demo)", Toast.LENGTH_SHORT).show();
                    refreshList();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ======= 以下為「示範階段」的 stub；日後接資料層時把它們替換即可 =======
    private void insertMenu(String name, double price, String imageUri) {
        // TODO integrate with SQLite/Repository
    }

    private void updateMenu(long id, String name, double price, String imageUri) {
        // TODO integrate with SQLite/Repository
    }

    private void deleteMenu(long id) {
        // TODO integrate with SQLite/Repository
    }

    private void refreshList() {
        // TODO 之後若改成 RecyclerView，這裡呼叫 adapter.notifyDataSetChanged()
        // 目前是「示範 UI」，你也可以先不做事或重新載入畫面。
    }
}