package com.example.medeasedesktop;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.StackPane;
import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class AdminDashboardController {

    @FXML
    private StackPane mainContent;

    @FXML
    private Label welcomeLabel;

    @FXML
    void manageDoctors(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Manage Doctors Section");
        mainContent.getChildren().add(lbl);
    }

    @FXML
    void approveAppointments(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Approve Appointments Section");
        mainContent.getChildren().add(lbl);
    }

    @FXML
    void manageSchedules(ActionEvent event) {
        mainContent.getChildren().clear();
        Label lbl = new Label("Manage Schedules Section");
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
        alert.setHeaderText("MedEase Admin Dashboard");
        alert.setContentText("This is a demo dashboard for admins in MedEase.");
        alert.show();
    }
}
