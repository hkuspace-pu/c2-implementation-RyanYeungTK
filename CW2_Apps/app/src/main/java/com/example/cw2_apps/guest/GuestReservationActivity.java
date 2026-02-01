package com.example.cw2_apps.guest;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.cw2_apps.data.repo.ReservationRepository;
import com.example.cw2_apps.data.session.SessionStore;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.util.Calendar;
import java.util.Locale;
import java.text.SimpleDateFormat;
import android.widget.Toast;
import com.example.cw2_apps.R;
import com.google.android.material.appbar.MaterialToolbar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;


public class GuestReservationActivity extends AppCompatActivity {
    private TextInputEditText etDate, etTime, etPeople;
    private MaterialAutoCompleteTextView etLocation, etPeopleGroup;
    private TextInputLayout tilPeople;
    private Button btnConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guest_make_reservation);

        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationOnClickListener(v -> finish());
        requestNotificationPermissionIfNeeded();

        etDate = findViewById(R.id.etDate);
        etTime = findViewById(R.id.etTime);
        etLocation = findViewById(R.id.etLocation);
        etPeopleGroup = findViewById(R.id.etPeopleGroup);
        tilPeople = findViewById(R.id.tilPeople);
        btnConfirm = findViewById(R.id.btnConfirm);
        String[] stores = new String[]{"Sha Tin", "Kai Tak", "Mong Kok"};
        String[] groups = new String[]{"1-2 people", "3-4 people", "5-8 people", "more than 8 people"};

        etLocation.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, stores));
        etPeopleGroup.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, groups));

        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) -> {
                etDate.setText(y + "-" + pad(m+1) + "-" + pad(d));
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Time Picker
        etTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, min) -> {
                etTime.setText(pad(h) + ":" + pad(min));
            }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });

        btnConfirm.setOnClickListener(v -> {
            tilPeople.setError(null);
            String username = SessionStore.username(this);
            String dateStr = textOf(etDate);
            String timeStr = textOf(etTime);
            String location = textOf(etLocation);
            String group = textOf(etPeopleGroup);

            if (dateStr.isEmpty() || timeStr.isEmpty() || location.isEmpty() || group.isEmpty()) {
                Toast.makeText(this, R.string.required, Toast.LENGTH_SHORT).show();
                return;
            }
            int partySize = approxPartySize(group);
            if (partySize < 1) { tilPeople.setError(getString(R.string.invalid_people)); return; }

            long when = toEpoch(dateStr, timeStr); // yyyy-MM-dd  HH:mm
            if (when <= System.currentTimeMillis()) {
                Toast.makeText(this, R.string.invalid_time, Toast.LENGTH_SHORT).show();
                return;
            }
            long rowId = new ReservationRepository(this)
                    .create(username, when, partySize, location, null);
            scheduleReminder(this, (int)rowId, when - 30 * 60 * 1000L,
                    getString(R.string.reminder_title),
                    getString(R.string.reminder_text, location));
            Toast.makeText(this, R.string.reservation_created, Toast.LENGTH_SHORT).show();
            finish();
        });

    }
    private static String textOf(TextInputEditText et){ return et.getText()==null? "" : et.getText().toString().trim(); }
    private static String textOf(MaterialAutoCompleteTextView et){ return et.getText()==null? "" : et.getText().toString().trim(); }
    private static String pad(int v){ return (v<10? "0":"") + v; }
    private static String val(TextInputEditText et){ return et.getText()==null? "" : et.getText().toString().trim(); }
    private static String val(MaterialAutoCompleteTextView et){ return et.getText()==null? "" : et.getText().toString().trim(); }
    private void requestNotificationPermissionIfNeeded(){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1001);
            }
        }
    }

    private static int approxPartySize(String group){
        String g = group.toLowerCase(Locale.US).replace(" ", "");
        if (g.startsWith("1-2")) return 2;
        if (g.startsWith("3-4")) return 4;
        if (g.startsWith("5-8")) return 8;
        return 12; // more than 8
    }
    private static long toEpoch(String yyyyMMdd, String HHmm){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            return sdf.parse(yyyyMMdd + " " + HHmm).getTime();
        } catch (Exception e) { return -1L; }
    }

    private static void scheduleReminder(Context ctx, int id, long triggerAtMillis, String title, String text){
        long now = System.currentTimeMillis();
        long when = triggerAtMillis <= now ? now + 30_000L : triggerAtMillis;

        AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent i = new Intent(ctx, com.example.cw2_apps.notifications.NotificationPublisher.class);
        i.putExtra("title", title);
        i.putExtra("text", text);
        i.putExtra("notifId", id);
        PendingIntent pi = PendingIntent.getBroadcast(
                ctx,
                id,
                i,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        boolean canExact = canUseExactAlarms(ctx, am);
        if (canExact) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            } else {
                am.setExact(AlarmManager.RTC_WAKEUP, when, pi);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, when, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, when, pi);
            }
            openExactAlarmSettings(ctx);
        }
    }

    private static boolean canUseExactAlarms(Context ctx, AlarmManager am){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return am.canScheduleExactAlarms();
        }
        return true;
    }

    private static void openExactAlarmSettings(Context ctx){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Intent intent = new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try { ctx.startActivity(intent); } catch (Exception ignored) {}
        }
    }

}
