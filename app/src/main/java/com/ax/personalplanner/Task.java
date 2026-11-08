package com.ax.personalplanner;

public class Task {
    public long id;
    public String title;
    public String note;
    public long dueAt;
    public int reminderMinutes;
    public String status;

    public Task(long id, String title, String note, long dueAt, int reminderMinutes, String status) {
        this.id = id;
        this.title = title;
        this.note = note;
        this.dueAt = dueAt;
        this.reminderMinutes = reminderMinutes;
        this.status = status;
    }
}
