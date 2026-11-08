package com.ax.personalplanner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            TaskDb db = new TaskDb(context);
            for (Task task : db.getAll()) {
                if (!"DONE".equals(task.status)) ReminderScheduler.schedule(context, task);
            }
            db.close();
        }
    }
}
