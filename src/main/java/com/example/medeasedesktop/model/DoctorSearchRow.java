package com.example.medeasedesktop.model;

public class DoctorSearchRow {

    private final int id;
    private final String name;
    private final String email;
    private final String specialty;

    public DoctorSearchRow(int id, String name, String email, String specialty) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.specialty = specialty;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getSpecialty() {
        return specialty;
    }
}
