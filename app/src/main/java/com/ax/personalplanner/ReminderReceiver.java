package com.ax.personalplanner;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "ax_planner_reminders";

    @Override public void onReceive(Context context, Intent intent) {
        String title = intent.getStringExtra("title");
        String note = intent.getStringExtra("note");
        long taskId = intent.getLongExtra("task_id", 0);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Nhắc nhở kế hoạch", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Thông báo nhắc các công việc sắp đến hạn");
            nm.createNotificationChannel(channel);
        }

        android.app.Notification.Builder builder = Build.VERSION.SDK_INT >= 26
                ? new android.app.Notification.Builder(context, CHANNEL_ID)
                : new android.app.Notification.Builder(context);

        builder.setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("AX PERSONAL PLANNER")
                .setContentText(title == null ? "Bạn có công việc sắp đến hạn." : title)
                .setStyle(new android.app.Notification.BigTextStyle()
                        .bigText((title == null ? "" : title) +
                                (note == null || note.trim().isEmpty() ? "" : "\n" + note)))
                .setAutoCancel(true);

        nm.notify((int) (taskId == 0 ? System.currentTimeMillis() : taskId), builder.build());
    }
}
