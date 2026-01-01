package com.example.medeasedesktop;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DoctorDashboardController {

    @FXML
    private BorderPane contentHolder;

    @FXML
    private Label doctorNameLabel;

    @FXML
    public void initialize() {
        doctorNameLabel.setText(Session.getFullName() == null ? "Doctor" : Session.getFullName() );
        loadView("shared/dashboard.fxml");
    }



    @FXML
    void showDashboard(ActionEvent event) {
        loadView("shared/dashboard.fxml");
    }

    @FXML
    void showAppointments(ActionEvent event) {
        loadView("shared/appointments.fxml");
    }

    @FXML
    void showPatientProfile(ActionEvent event) {
        loadView("shared/patient_search.fxml");
    }

    @FXML
    void showQueue(ActionEvent event) {
        loadView("doctor/queue_view.fxml");
    }

    @FXML
    void showPrescriptions(ActionEvent event) {
        loadView("doctor/prescriptions.fxml");
    }

    @FXML
    void showMyProfile(ActionEvent event) {
        loadView("doctor/my_profile.fxml");
    }

    private void loadView(String path) {
        try {
            Node view = FXMLLoader.load(getClass().getResource(path));
            contentHolder.setCenter(view);
        } catch (Exception ex) {
            ex.printStackTrace();
            contentHolder.setCenter(new Label("Failed to load: " + path));
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to logout.").show();
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
        alert.setContentText(
                "Doctor workspace:\n" +
                        "• Appointments\n" +
                        "• Patient queue\n" +
                        "• Prescriptions\n" +
                        "• Patient profiles"
        );
        alert.show();
    }

    private Stage getStageFromEvent(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof MenuItem menuItem) {
            return (Stage) menuItem.getParentPopup().getOwnerWindow();
        }
        return (Stage) ((Node) src).getScene().getWindow();
    }
}
