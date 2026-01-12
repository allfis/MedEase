package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.PatientDAO;
import com.example.medeasedesktop.dao.SignupDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class SignupController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;
    @FXML private Label statusLabel;

    private final SignupDAO signupDAO = new SignupDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    @FXML
    void handleSignup(ActionEvent event) {
        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String phone = phoneField.getText() == null ? "" : phoneField.getText().trim();
        String pass = passwordField.getText() == null ? "" : passwordField.getText();
        String conf = confirmField.getText() == null ? "" : confirmField.getText();

        statusLabel.setText("");

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("Name, email, and password are required.");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
            statusLabel.setText("Enter a valid email.");
            return;
        }

        if (!pass.equals(conf)) {
            statusLabel.setText("Passwords do not match.");
            return;
        }

        try {
            Integer newUserId = signupDAO.createUser("PATIENT", email, pass, name);
            if (newUserId == null) {
                statusLabel.setText("Email already exists or signup failed.");
                return;
            }

            boolean ok = patientDAO.createPatientRow(newUserId, name, email, phone);
            if (!ok) {
                statusLabel.setText("Patient profile create failed.");
                return;
            }

            statusLabel.setText("Account created. Please login.");

        } catch (Exception ex) {
            statusLabel.setText("Signup error: " + ex.getMessage());
        }
    }

    @FXML
    void goBack(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open login.").show();
        }
    }
}
