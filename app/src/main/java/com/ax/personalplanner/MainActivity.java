package com.ax.personalplanner;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {
    private TaskDb db;
    private LinearLayout listBox;
    private TextView summary;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN"));

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new TaskDb(this);
        requestNotificationPermission();
        buildScreen();
        refresh();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 &&
                checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
        }
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, int size, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value);
        t.setTextSize(size);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setTextColor(0xFF202124);
        return t;
    }

    private void buildScreen() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(18), dp(18), dp(12));
        root.setBackgroundColor(0xFFF7F8FA);

        TextView heading = text("AX PERSONAL PLANNER", 22, true);
        root.addView(heading);

        TextView subtitle = text("Sổ kế hoạch cá nhân", 14, false);
        subtitle.setTextColor(0xFF687078);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-1, -2);
        subLp.topMargin = dp(4);
        root.addView(subtitle, subLp);

        summary = text("", 14, true);
        summary.setPadding(dp(12), dp(12), dp(12), dp(12));
        summary.setBackgroundColor(0xFFE9EEF5);
        LinearLayout.LayoutParams sumLp = new LinearLayout.LayoutParams(-1, -2);
        sumLp.topMargin = dp(16);
        root.addView(summary, sumLp);

        Button add = new Button(this);
        add.setText("+  THÊM CÔNG VIỆC");
        LinearLayout.LayoutParams addLp = new LinearLayout.LayoutParams(-1, -2);
        addLp.topMargin = dp(12);
        root.addView(add, addLp);
        add.setOnClickListener(v -> showEditor(null));

        TextView section = text("CÔNG VIỆC CỦA TÔI", 15, true);
        LinearLayout.LayoutParams secLp = new LinearLayout.LayoutParams(-1, -2);
        secLp.topMargin = dp(18);
        secLp.bottomMargin = dp(8);
        root.addView(section, secLp);

        ScrollView scroll = new ScrollView(this);
        listBox = new LinearLayout(this);
        listBox.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(listBox);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        TextView footer = text("Lưu trực tiếp trên điện thoại • Không cần Internet", 11, false);
        footer.setGravity(Gravity.CENTER);
        footer.setTextColor(0xFF7B828A);
        LinearLayout.LayoutParams footLp = new LinearLayout.LayoutParams(-1, -2);
        footLp.topMargin = dp(8);
        root.addView(footer, footLp);

        setContentView(root);
    }

    private void refresh() {
        List<Task> tasks = db.getAll();
        int active = 0, done = 0, upcoming = 0, overdue = 0;
        long now = System.currentTimeMillis();
        for (Task t : tasks) {
            if ("DONE".equals(t.status)) done++;
            else {
                active++;
                if (t.dueAt < now) overdue++;
                else if (t.dueAt - now <= 3L * 24 * 60 * 60 * 1000) upcoming++;
            }
        }
        summary.setText("Đang theo dõi: " + active + "/15     Sắp đến hạn: " + upcoming +
                "     Quá hạn: " + overdue + "     Đã xong: " + done);

        listBox.removeAllViews();
        if (tasks.isEmpty()) {
            TextView empty = text("Chưa có kế hoạch nào. Nhấn “THÊM CÔNG VIỆC” để bắt đầu.", 14, false);
            empty.setPadding(dp(12), dp(20), dp(12), dp(20));
            listBox.addView(empty);
            return;
        }

        for (Task task : tasks) {
            listBox.addView(createTaskCard(task));
        }
    }

    private View createTaskCard(Task task) {
        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(dp(14), dp(12), dp(14), dp(10));
        card.setBackgroundColor(0xFFFFFFFF);
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(-1, -2);
        cardLp.bottomMargin = dp(9);
        card.setElevation(dp(1));

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        TextView title = text(task.title, 16, true);
        title.setMaxLines(2);
        top.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        TextView status = text(statusLabel(task.status), 12, true);
        status.setTextColor(statusColor(task.status));
        top.addView(status);
        card.addView(top);

        TextView due = text("Thực hiện: " + dateFormat.format(new Date(task.dueAt)), 13, false);
        due.setTextColor(0xFF59636E);
        LinearLayout.LayoutParams dueLp = new LinearLayout.LayoutParams(-1, -2);
        dueLp.topMargin = dp(6);
        card.addView(due, dueLp);

        if (task.note != null && !task.note.trim().isEmpty()) {
            TextView note = text(task.note, 13, false);
            note.setTextColor(0xFF59636E);
            LinearLayout.LayoutParams noteLp = new LinearLayout.LayoutParams(-1, -2);
            noteLp.topMargin = dp(4);
            card.addView(note, noteLp);
        }

        LinearLayout actions = new LinearLayout(this);
        actions.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);
        actions.setOrientation(LinearLayout.HORIZONTAL);

        Button statusBtn = smallButton("Trạng thái");
        statusBtn.setOnClickListener(v -> chooseStatus(task));
        actions.addView(statusBtn);

        Button editBtn = smallButton("Sửa");
        editBtn.setOnClickListener(v -> showEditor(task));
        actions.addView(editBtn);

        Button deleteBtn = smallButton("Xóa");
        deleteBtn.setOnClickListener(v -> confirmDelete(task));
        actions.addView(deleteBtn);

        LinearLayout.LayoutParams actLp = new LinearLayout.LayoutParams(-1, -2);
        actLp.topMargin = dp(5);
        card.addView(actions, actLp);
        return card;
    }

    private Button smallButton(String label) {
        Button b = new Button(this);
        b.setText(label);
        b.setTextSize(11);
        b.setMinHeight(dp(36));
        b.setMinimumHeight(dp(36));
        b.setPadding(dp(5), 0, dp(5), 0);
        return b;
    }

    private String statusLabel(String s) {
        switch (s) {
            case "DOING": return "Đang làm";
            case "PAUSED": return "Tạm hoãn";
            case "DONE": return "Hoàn thành";
            default: return "Chưa làm";
        }
    }

    private int statusColor(String s) {
        if ("DONE".equals(s)) return 0xFF188038;
        if ("PAUSED".equals(s)) return 0xFF8A5A00;
        if ("DOING".equals(s)) return 0xFF1967D2;
        return 0xFF5F6368;
    }

    private void chooseStatus(Task task) {
        String[] labels = {"Chưa thực hiện", "Đang thực hiện", "Tạm hoãn", "Đã hoàn thành"};
        String[] values = {"TODO", "DOING", "PAUSED", "DONE"};
        new AlertDialog.Builder(this)
                .setTitle("Cập nhật trạng thái")
                .setItems(labels, (d, which) -> {
                    task.status = values[which];
                    db.setStatus(task.id, task.status);
                    if ("DONE".equals(task.status)) ReminderScheduler.cancel(this, task.id);
                    else ReminderScheduler.schedule(this, task);
                    refresh();
                }).show();
    }

    private void confirmDelete(Task task) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa công việc?")
                .setMessage("Bạn có chắc muốn xóa “" + task.title + "”?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (d, w) -> {
                    ReminderScheduler.cancel(this, task.id);
                    db.delete(task.id);
                    refresh();
                }).show();
    }

    private void showEditor(Task existing) {
        if (existing == null && db.countActive() >= 15) {
            new AlertDialog.Builder(this).setMessage(
                    "Bạn đã có đủ 15 công việc đang theo dõi. Hãy hoàn thành hoặc xóa một việc trước khi thêm mới.")
                    .setPositiveButton("Đóng", null).show();
            return;
        }

        LinearLayout form = new LinearLayout(this);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setPadding(dp(20), dp(8), dp(20), dp(4));

        EditText title = new EditText(this);
        title.setSingleLine(true);
        title.setHint("Tên công việc *");
        title.setText(existing == null ? "" : existing.title);
        form.addView(title);

        EditText note = new EditText(this);
        note.setHint("Nội dung / ghi chú (không bắt buộc)");
        note.setMinLines(2);
        note.setGravity(Gravity.TOP);
        note.setText(existing == null ? "" : existing.note);
        form.addView(note);

        Calendar selected = Calendar.getInstance();
        if (existing != null) selected.setTimeInMillis(existing.dueAt);
        else selected.add(Calendar.DAY_OF_MONTH, 1);

        TextView dateTime = text("", 14, true);
        dateTime.setPadding(0, dp(10), 0, dp(8));
        updateDateLabel(dateTime, selected);
        form.addView(dateTime);

        Button pickDate = smallButton("Chọn ngày và giờ thực hiện");
        form.addView(pickDate);
        pickDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        selected.set(Calendar.YEAR, year);
                        selected.set(Calendar.MONTH, month);
                        selected.set(Calendar.DAY_OF_MONTH, day);
                        updateDateLabel(dateTime, selected);
                    }, selected.get(Calendar.YEAR), selected.get(Calendar.MONTH),
                    selected.get(Calendar.DAY_OF_MONTH));
            dpd.show();
        });

        Button pickTime = smallButton("Chọn giờ");
        form.addView(pickTime);
        pickTime.setOnClickListener(v -> {
            new TimePickerDialog(this, (view, hour, minute) -> {
                selected.set(Calendar.HOUR_OF_DAY, hour);
                selected.set(Calendar.MINUTE, minute);
                selected.set(Calendar.SECOND, 0);
                updateDateLabel(dateTime, selected);
            }, selected.get(Calendar.HOUR_OF_DAY), selected.get(Calendar.MINUTE), true).show();
        });

        TextView reminderLabel = text("Nhắc trước thời điểm thực hiện", 13, true);
        LinearLayout.LayoutParams rl = new LinearLayout.LayoutParams(-1, -2);
        rl.topMargin = dp(10);
        form.addView(reminderLabel, rl);

        Spinner reminder = new Spinner(this);
        String[] reminderLabels = {"Đúng giờ", "Trước 30 phút", "Trước 1 giờ", "Trước 1 ngày", "Trước 2 ngày", "Trước 3 ngày"};
        int[] reminderValues = {0, 30, 60, 1440, 2880, 4320};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, reminderLabels);
        reminder.setAdapter(adapter);
        int selectedIndex = 3;
        if (existing != null) {
            for (int i = 0; i < reminderValues.length; i++)
                if (reminderValues[i] == existing.reminderMinutes) selectedIndex = i;
        }
        reminder.setSelection(selectedIndex);
        form.addView(reminder);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(existing == null ? "Thêm công việc" : "Sửa công việc")
                .setView(form)
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Lưu", null)
                .create();

        dialog.setOnShowListener(x -> dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String taskTitle = title.getText().toString().trim();
            if (taskTitle.isEmpty()) {
                title.setError("Vui lòng nhập tên công việc");
                return;
            }
            Task task = existing == null
                    ? new Task(0, taskTitle, note.getText().toString().trim(),
                        selected.getTimeInMillis(), reminderValues[reminder.getSelectedItemPosition()], "TODO")
                    : new Task(existing.id, taskTitle, note.getText().toString().trim(),
                        selected.getTimeInMillis(), reminderValues[reminder.getSelectedItemPosition()], existing.status);

            long id = db.save(task);
            task.id = id;
            ReminderScheduler.schedule(this, task);
            dialog.dismiss();
            refresh();
        }));
        dialog.show();
    }

    private void updateDateLabel(TextView label, Calendar selected) {
        label.setText("Thời gian thực hiện: " +
                new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("vi", "VN")).format(selected.getTime()));
    }

    @Override protected void onDestroy() {
        if (db != null) db.close();
        super.onDestroy();
    }
}
