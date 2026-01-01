package com.example.medeasedesktop.model;

public class ReviewRow {
    private final int id;
    private final int doctorId;
    private final int patientId;
    private final String doctorName;
    private final String patientName;
    private final int rating;
    private final String comment;
    private final String createdAt;

    public ReviewRow(int id, int doctorId, int patientId,
                     String doctorName, String patientName,
                     int rating, String comment, String createdAt) {
        this.id = id;
        this.doctorId = doctorId;
        this.patientId = patientId;
        this.doctorName = doctorName;
        this.patientName = patientName;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getDoctorId() { return doctorId; }
    public int getPatientId() { return patientId; }
    public String getDoctorName() { return doctorName; }
    public String getPatientName() { return patientName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getCreatedAt() { return createdAt; }
}
