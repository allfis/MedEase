package com.example.medeasedesktop;

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
        String password = passwordField.isVisible()
                ? passwordField.getText()
                : showPasswordField.getText();

        if (roleGroup.getSelectedToggle() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select a role (Doctor/Admin).").show();
            return;
        }
        if (email.isEmpty() || password == null || password.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Gmail and password are required.").show();
            return;
        }
        if (!email.endsWith("@gmail.com")) {
            new Alert(Alert.AlertType.WARNING, "Enter a valid Gmail address.").show();
            return;
        }

        String fxmlToLoad = adminRadio.isSelected() ? "admin_dashboard.fxml" : "doctor_dashboard.fxml";

        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlToLoad));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open dashboard.").show();
        }
    }
}
