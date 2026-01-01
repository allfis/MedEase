package com.example.medeasedesktop.model;

public class DoctorProfile {
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String specialty;
    private String about;
    private double avgRating;
    private int totalReviews;
    private int monthReviews;

    public DoctorProfile(int userId, String fullName, String email, String phone, String specialty, String about,
                         double avgRating, int totalReviews, int monthReviews) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.specialty = specialty;
        this.about = about;
        this.avgRating = avgRating;
        this.totalReviews = totalReviews;
        this.monthReviews = monthReviews;
    }

    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public String getSpecialty() { return specialty; }
    public String getAbout() { return about; }
    public double getAvgRating() { return avgRating; }
    public int getTotalReviews() { return totalReviews; }
    public int getMonthReviews() { return monthReviews; }

    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setEmail(String email) { this.email = email; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public void setAbout(String about) { this.about = about; }
}
