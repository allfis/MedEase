package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private RadioButton doctorRadio;

    @FXML
    private RadioButton adminRadio;

    @FXML
    private ToggleGroup roleGroup;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField showPasswordField;

    @FXML
    private CheckBox showPasswordCheck;

    @FXML
    void togglePassword() {
        if (showPasswordCheck.isSelected()) {
            showPasswordField.setText(passwordField.getText());
            showPasswordField.setManaged(true);
            showPasswordField.setVisible(true);
            passwordField.setManaged(false);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(showPasswordField.getText());
            passwordField.setManaged(true);
            passwordField.setVisible(true);
            showPasswordField.setManaged(false);
            showPasswordField.setVisible(false);
        }
    }

    @FXML
    void handleLogin(ActionEvent event) {
        String email = emailField.getText() == null ? "" : emailField.getText().trim();
        String password = passwordField.isVisible() ? passwordField.getText() : showPasswordField.getText();

        if (roleGroup.getSelectedToggle() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a role (Doctor/Admin).").show();
            return;
        }

        if (email.isEmpty() || password == null || password.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Email and password are required.").show();
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid email address.").show();
            return;
        }
        String role = adminRadio.isSelected() ? "ADMIN" : "DOCTOR";

        UserDAO dao = new UserDAO();
        if (!dao.authenticateAndLoad(role, email, password)) {
            new Alert(Alert.AlertType.ERROR, "Invalid credentials.").show();
            return;
        }



        String fxmlToLoad = adminRadio.isSelected() ? "admin_dashboard.fxml" : "doctor_dashboard.fxml";

        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlToLoad));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Failed to open dashboard.").show();
        }
    }
}
