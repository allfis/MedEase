package com.example.medeasedesktop.model;

public class DoctorProfileRow {

    private final int doctorId;
    private final String name;
    private final String email;
    private final String specialty;
    private final String about;

    public DoctorProfileRow(int doctorId, String name, String email, String specialty, String about) {
        this.doctorId = doctorId;
        this.name = name == null ? "" : name;
        this.email = email == null ? "" : email;
        this.specialty = specialty == null ? "" : specialty;
        this.about = about == null ? "" : about;
    }

    public int getDoctorId() { return doctorId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSpecialty() { return specialty; }
    public String getAbout() { return about; }
}
