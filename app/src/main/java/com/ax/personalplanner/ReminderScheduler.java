package com.ax.personalplanner;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderScheduler {
    public static void schedule(Context context, Task task) {
        cancel(context, task.id);
        if ("DONE".equals(task.status)) return;

        long triggerAt = task.dueAt - task.reminderMinutes * 60_000L;
        if (triggerAt <= System.currentTimeMillis()) {
            // Không tạo thông báo quá hạn ngay khi lưu; chỉ nhắc nếu thời điểm nhắc vẫn còn ở tương lai.
            return;
        }

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("task_id", task.id);
        intent.putExtra("title", task.title);
        intent.putExtra("note", task.note);

        PendingIntent pi = PendingIntent.getBroadcast(context, (int) task.id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= 23) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        } else {
            am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
        }
    }

    public static void cancel(Context context, long id) {
        Intent intent = new Intent(context, ReminderReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, (int) id, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(pi);
        pi.cancel();
    }
}
