package com.example.cw2_apps.staff.ui;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2_apps.R;
import com.example.cw2_apps.databinding.ItemMenuBinding;
import com.example.cw2_apps.staff.model.MenuItem;
import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.VH> {
    public enum Role { STAFF, GUEST }
    private final Role role;
    public MenuAdapter() { this(Role.STAFF); }
    public MenuAdapter(Role role) { this.role = role; }

    public interface OnItemActionListener {
        void onEdit(MenuItem item, int position, View v);
        void onDelete(MenuItem item, int position, View v);
    }
    private final List<MenuItem> items = new ArrayList<>();
    private OnItemActionListener listener;
    public void setOnItemActionListener(OnItemActionListener l) { this.listener = l; }
    public void submitList(List<MenuItem> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }
    public void removeAt(int position) {
        if (position < 0 || position >= items.size()) return;
        items.remove(position);
        notifyItemRemoved(position);
    }
    public MenuItem getItem(int position) { return items.get(position); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMenuBinding b = ItemMenuBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        MenuItem m = items.get(pos);
        h.b.tvName.setText(m.name);
        h.b.tvPrice.setText(String.format("$%.0f", m.price));


        if (m.imageUri != null && m.imageUri.startsWith("res:")) {
            String entry = m.imageUri.substring(4);
            int resId = h.itemView.getResources()
                    .getIdentifier(entry, "drawable", h.itemView.getContext().getPackageName());
            if (resId != 0) {
                h.b.img.setImageResource(resId);
            } else {
                h.b.img.setImageResource(R.drawable.placeholder);
            }
        } else if (m.imageUri != null && !m.imageUri.isEmpty()) {
            try {
                h.b.img.setImageURI(android.net.Uri.parse(m.imageUri));
            } catch (Exception ignore) {
                h.b.img.setImageResource(R.drawable.placeholder);
            }
        } else {
            h.b.img.setImageResource(R.drawable.placeholder);
        }

        if (role == Role.GUEST) {
            h.b.btnEdit.setVisibility(View.GONE);
            h.b.btnDelete.setVisibility(View.GONE);
            h.b.btnEdit.setOnClickListener(null);
            h.b.btnDelete.setOnClickListener(null);
        } else {
            h.b.btnEdit.setVisibility(View.VISIBLE);
            h.b.btnDelete.setVisibility(View.VISIBLE);
            h.b.btnEdit.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(m, h.getBindingAdapterPosition(), v);
            });
            h.b.btnDelete.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(m, h.getBindingAdapterPosition(), v);
            });
        }

    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ItemMenuBinding b;
        VH(@NonNull ItemMenuBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }

}
