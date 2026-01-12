package com.example.medeasedesktop.model;

public class PatientAppointmentRow {

    private final String date;
    private final String time;
    private final String doctor;
    private final String status;

    public PatientAppointmentRow(String date, String time, String doctor, String status) {
        this.date = date;
        this.time = time;
        this.doctor = doctor;
        this.status = status;
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
