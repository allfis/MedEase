package com.example.medeasedesktop.model;

public class AppointmentRow {
    private final int id;
    private final String patient;
    private final String date;
    private final String time;
    private final String doctor;
    private final String status;

    public AppointmentRow(int id, String patient, String date, String time, String doctor, String status) {
        this.id = id;
        this.patient = patient;
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getPatient() {
        return patient;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getDoctor() {
        return doctor;
    }

    public String getStatus() {
        return status;
    }
}
