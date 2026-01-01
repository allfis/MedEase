package com.example.medeasedesktop.model;

public class PatientRow {
    private final int id;
    private final String fullName;
    private final String phone;
    private final String gender;
    private final int age;

    public PatientRow(int id, String fullName, String phone, String gender, int age) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.gender = gender;
        this.age = age;
    }

    public int getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getGender() { return gender; }
    public int getAge() { return age; }
}
