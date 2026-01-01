package com.example.medeasedesktop.model;

public class ActivityRow {
    private final String time;
    private final String event;
    private final String user;
    private final String details;

    public ActivityRow(String time, String event, String user, String details) {
        this.time = time;
        this.event = event;
        this.user = user;
        this.details = details;
    }

    public String getTime() { return time; }
    public String getEvent() { return event; }
    public String getUser() { return user; }
    public String getDetails() { return details; }
}
