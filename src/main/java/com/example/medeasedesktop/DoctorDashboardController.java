package com.example.medeasedesktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.stage.Stage;


public class DoctorDashboardController {
    @FXML
    private StackPane mainContent;

    @FXML
    private Label welcomeLabel;

    @FXML
    void showAppointments(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Here are today's appointments");
        mainContent.getChildren().add(lbl);
    }

    @FXML
    void showQueue(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Patient queue will appear here");
        mainContent.getChildren().add(lbl);
    }

    @FXML
    void showPrescriptions(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Prescriptions section");
        mainContent.getChildren().add(lbl);
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            MenuItem menuItem = (MenuItem) event.getSource();
            Stage stage = (Stage) menuItem.getParentPopup().getOwnerWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void handleAbout(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About MedEase");
        alert.setHeaderText("MedEase Doctor Dashboard");
        alert.setContentText("This is a demo dashboard for doctors in MedEase.");
        alert.show();
    }
}
