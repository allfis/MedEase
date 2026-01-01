package com.example.medeasedesktop.model;

public class ReviewRow {
    private final int id;
    private final String patient;
    private final String doctor;
    private final int rating;
    private final String comment;
    private final String date;

    public ReviewRow(int id, String patient, String doctor, int rating, String comment, String date) {
        this.id = id;
        this.patient = patient;
        this.doctor = doctor;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public String getPatient() {
        return patient;
    }

    public String getDoctor() {
        return doctor;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }
}
