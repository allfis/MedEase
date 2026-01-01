package com.example.medeasedesktop.model;

public class ScheduleRow {
    private final int id;
    private final int doctorId;
    private final String doctorName;
    private final String day;
    private final String startTime;
    private final String endTime;
    private final String type;
    private final String createdAt;

    public ScheduleRow(int id, int doctorId, String doctorName,
                             String day, String startTime, String endTime,
                             String type, String createdAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getDoctorId() { return doctorId; }
    public String getDoctorName() { return doctorName; }
    public String getDay() { return day; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getType() { return type; }
    public String getCreatedAt() { return createdAt; }
}
