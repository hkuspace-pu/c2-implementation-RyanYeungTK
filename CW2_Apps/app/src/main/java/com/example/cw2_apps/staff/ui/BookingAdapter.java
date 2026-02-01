package com.example.cw2_apps.staff.ui;

import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cw2_apps.R;
import com.example.cw2_apps.databinding.ItemBookingBinding;
import com.example.cw2_apps.staff.model.Reservation;
import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    public enum Role { STAFF, GUEST }

    public interface OnItemActionListener {
        void onConfirm(Reservation r, int position, View sourceView);
        void onCancel(Reservation r, int position, View sourceView);
    }

    private final List<Reservation> items = new ArrayList<>();
    private OnItemActionListener listener;
    private final Role role;

    public BookingAdapter() { this(Role.STAFF); }
    public BookingAdapter(Role role) { this.role = role; }

    public void setOnItemActionListener(OnItemActionListener l) { this.listener = l; }

    public void submitList(List<Reservation> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    public Reservation getItem(int position) { return items.get(position); }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        ItemBookingBinding b = ItemBookingBinding.inflate(inf, parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Reservation r = items.get(pos);

        h.b.tvDateTime.setText(r.datetime);
        h.b.tvPeopleLocation.setText(r.party);
        applyStatusStyle(h.b.tvStatus, r.status);

        if (role == Role.STAFF) {
            if ("CONFIRMED".equals(r.status)) {
                h.b.btnPrimary.setText(R.string.confirmed);
                h.b.btnPrimary.setEnabled(false);
            } else {
                h.b.btnPrimary.setText(R.string.confirm);
                h.b.btnPrimary.setEnabled(true);
            }
            h.b.btnSecondary.setText(R.string.cancel);
        } else { // GUEST
            h.b.btnPrimary.setText(R.string.edit);
            h.b.btnPrimary.setEnabled(true);
            h.b.btnSecondary.setText(R.string.cancel);
        }

        h.b.btnPrimary.setOnClickListener(v -> {
            if (listener != null) listener.onConfirm(r, h.getBindingAdapterPosition(), v);
        });
        h.b.btnSecondary.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(r, h.getBindingAdapterPosition(), v);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    public void updateItemStatus(int position, String newStatus) {
        if (position < 0 || position >= items.size()) return;
        items.get(position).status = newStatus;
        notifyItemChanged(position);
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemBookingBinding b;
        VH(@NonNull ItemBookingBinding binding) {
            super(binding.getRoot());
            this.b = binding;
        }
    }

    private void applyStatusStyle(android.widget.TextView tv, String status) {
        View view = tv;
        int bg; int fg;

        switch (status) {
            case "CONFIRMED": {
                bg = getMaterialColorOrFallback(view, R.attr.colorPrimary, R.color.status_confirmed_bg);
                fg = ContextCompat.getColor(view.getContext(), R.color.status_on_dark);
                break;
            }
            case "CANCELLED": {
                bg = getMaterialColorOrFallback(view, R.attr.colorError, R.color.status_cancelled_bg);
                Integer onError = tryGetMaterialColor(view, R.attr.colorOnError);
                fg = onError != null ? onError
                        : ContextCompat.getColor(view.getContext(), R.color.status_on_dark);
                break;
            }
            default: {
                bg = getMaterialColorOrFallback(view, R.attr.colorSecondaryContainer, R.color.status_neutral_bg);
                Integer onSec = tryGetMaterialColor(view, R.attr.colorOnSecondaryContainer);
                fg = onSec != null ? onSec
                        : ContextCompat.getColor(view.getContext(), R.color.status_on_light);
            }
        }

        tv.setText(status);
        Drawable d = DrawableCompat.wrap(tv.getBackground()).mutate();
        DrawableCompat.setTint(d, bg);
        tv.setBackground(d);
        tv.setTextColor(fg);
    }

    private int getMaterialColorOrFallback(View view, int materialAttr, int fallbackColorRes) {
        Integer c = tryGetMaterialColor(view, materialAttr);
        return (c != null) ? c : ContextCompat.getColor(view.getContext(), fallbackColorRes);
    }
    private Integer tryGetMaterialColor(View view, int materialAttr) {
        try { return MaterialColors.getColor(view, materialAttr); }
        catch (Exception ignore) { return null; }
    }
}