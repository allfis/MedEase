package com.example.medeasedesktop.model;

public class SystemStats {
    private final int todayAppointments;
    private final int approved;
    private final int rejectedOrCancelled;
    private final int activeDoctors;
    private final int onLeaveToday;
    private final int newReviewsToday;
    private final String generatedAt;

    public SystemStats(int todayAppointments, int approved, int rejectedOrCancelled,
                       int activeDoctors, int onLeaveToday, int newReviewsToday,
                       String generatedAt) {
        this.todayAppointments = todayAppointments;
        this.approved = approved;
        this.rejectedOrCancelled = rejectedOrCancelled;
        this.activeDoctors = activeDoctors;
        this.onLeaveToday = onLeaveToday;
        this.newReviewsToday = newReviewsToday;
        this.generatedAt = generatedAt;
    }

    public int getTodayAppointments() { return todayAppointments; }
    public int getApproved() { return approved; }
    public int getRejectedOrCancelled() { return rejectedOrCancelled; }
    public int getActiveDoctors() { return activeDoctors; }
    public int getOnLeaveToday() { return onLeaveToday; }
    public int getNewReviewsToday() { return newReviewsToday; }
    public String getGeneratedAt() { return generatedAt; }
}
