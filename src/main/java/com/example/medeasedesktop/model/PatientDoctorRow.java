package com.example.medeasedesktop.model;

public class PatientDoctorRow {

    private final int doctorId;
    private final String name;
    private final String email;
    private final String specialty;
    private final String hospital;

    public PatientDoctorRow(int doctorId, String name, String email, String specialty, String hospital) {
        this.doctorId = doctorId;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
        this.hospital = hospital;
    }

    public int getDoctorId() { return doctorId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getSpecialty() { return specialty; }
    public String getHospital() { return hospital; }
}
