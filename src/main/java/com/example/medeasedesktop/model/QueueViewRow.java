package com.example.medeasedesktop.model;

public class QueueViewRow {
    public final String time;
    public final String patient;
    public final String status;

    public QueueViewRow(String time, String patient, String status) {
        this.time = time;
        this.patient = patient;
        this.status = status;
    }
}
