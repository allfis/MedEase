package com.example.medeasedesktop.model;

import javafx.beans.property.*;

public class DoctorRow {
    private final IntegerProperty id = new SimpleIntegerProperty();
    private final StringProperty fullName = new SimpleStringProperty();
    private final StringProperty email = new SimpleStringProperty();
    private final StringProperty status = new SimpleStringProperty();

    public DoctorRow(int id, String fullName, String email, String status) {
        this.id.set(id);
        this.fullName.set(fullName);
        this.email.set(email);
        this.status.set(status);
    }

    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getFullName() { return fullName.get(); }
    public StringProperty fullNameProperty() { return fullName; }

    public String getEmail() { return email.get(); }
    public StringProperty emailProperty() { return email; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
