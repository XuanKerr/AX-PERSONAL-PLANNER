package com.ax.personalplanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDb extends SQLiteOpenHelper {
    public TaskDb(Context context) {
        super(context, "ax_personal_planner.db", null, 1);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT NOT NULL," +
                "note TEXT," +
                "due_at INTEGER NOT NULL," +
                "reminder_minutes INTEGER NOT NULL," +
                "status TEXT NOT NULL)");
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }

    public List<Task> getAll() {
        List<Task> list = new ArrayList<>();
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT id,title,note,due_at,reminder_minutes,status FROM tasks " +
                "ORDER BY CASE WHEN status='DONE' THEN 1 ELSE 0 END, due_at ASC", null);
        try {
            while (c.moveToNext()) {
                list.add(new Task(c.getLong(0), c.getString(1), c.getString(2),
                        c.getLong(3), c.getInt(4), c.getString(5)));
            }
        } finally { c.close(); }
        return list;
    }

    public int countActive() {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM tasks WHERE status!='DONE'", null);
        try { return c.moveToFirst() ? c.getInt(0) : 0; }
        finally { c.close(); }
    }

    public long save(Task task) {
        ContentValues v = new ContentValues();
        v.put("title", task.title);
        v.put("note", task.note);
        v.put("due_at", task.dueAt);
        v.put("reminder_minutes", task.reminderMinutes);
        v.put("status", task.status);
        if (task.id == 0) {
            return getWritableDatabase().insert("tasks", null, v);
        }
        getWritableDatabase().update("tasks", v, "id=?", new String[]{String.valueOf(task.id)});
        return task.id;
    }

    public void setStatus(long id, String status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        getWritableDatabase().update("tasks", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void delete(long id) {
        getWritableDatabase().delete("tasks", "id=?", new String[]{String.valueOf(id)});
    }
}
