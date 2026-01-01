package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.DoctorProfileDAO;
import com.example.medeasedesktop.model.DoctorProfile;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DoctorProfileController {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField specialtyField;
    @FXML private TextArea aboutArea;

    @FXML private Label avgRatingLabel;
    @FXML private Label totalReviewsLabel;
    @FXML private Label monthReviewsLabel;

    private DoctorProfileDAO dao = new DoctorProfileDAO();
    private DoctorProfile current;

    @FXML
    public void initialize() {
        load();
    }

    private void load() {
        current = dao.getByUserId(Session.getUserId());
        if (current == null) {
            new Alert(Alert.AlertType.ERROR, "Doctor profile not found.").show();
            return;
        }

        fullNameField.setText(nv(current.getFullName()));
        emailField.setText(nv(current.getEmail()));
        phoneField.setText(nv(current.getPhone()));
        specialtyField.setText(nv(current.getSpecialty()));
        aboutArea.setText(nv(current.getAbout()));

        avgRatingLabel.setText(String.format("%.1f", current.getAvgRating()));
        totalReviewsLabel.setText(String.valueOf(current.getTotalReviews()));
        monthReviewsLabel.setText(String.valueOf(current.getMonthReviews()));
    }

    private String nv(String s) {
        return s == null ? "" : s;
    }

    @FXML
    void save() {
        if (current == null) return;

        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid email address.").show();
            return;
        }

        current.setFullName(fullNameField.getText() == null ? "" : fullNameField.getText().trim());
        current.setEmail(email);
        current.setPhone(phoneField.getText() == null ? "" : phoneField.getText().trim());
        current.setSpecialty(specialtyField.getText() == null ? "" : specialtyField.getText().trim());
        current.setAbout(aboutArea.getText() == null ? "" : aboutArea.getText().trim());

        boolean ok = dao.upsertProfile(current);
        if (ok) {
            Session.set(Session.getUserId(), Session.getRole(), current.getEmail(), current.getFullName());
            new Alert(Alert.AlertType.INFORMATION, "Profile saved.").show();
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to save profile.").show();
        }
    }

    @FXML
    void reset() {
        load();
    }
}
