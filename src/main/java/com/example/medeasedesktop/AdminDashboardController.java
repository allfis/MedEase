package com.example.medeasedesktop;

import com.example.medeasedesktop.Session;
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

public class AdminDashboardController {

    @FXML
    private BorderPane contentHolder;

    @FXML
    private Label adminNameLabel;

    @FXML
    public void initialize() {
        adminNameLabel.setText(Session.getFullName() == null ? "Admin" : Session.getFullName());
        contentHolder.getScene();
        contentHolder.getStylesheets().add(getClass().getResource("css/dashboard.css").toExternalForm());
        loadView("shared/dashboard.fxml");
    }



    @FXML
    void showDashboard(ActionEvent e) {
        loadView("shared/dashboard.fxml");
    }

    @FXML
    void viewPatients(ActionEvent e) {
        loadView("shared/patient_search.fxml");
    }

    @FXML
    void viewAppointments(ActionEvent e) {
        loadView("shared/appointments.fxml");
    }

    @FXML
    void manageDoctors(ActionEvent e) {
        loadView("admin/doctor_management.fxml");
    }

    @FXML
    void manageSchedules(ActionEvent e) {
        loadView("admin/schedule_management.fxml");
    }

    @FXML
    void viewReviews(ActionEvent e) {
        loadView("admin/reviews.fxml");
    }

    @FXML
    void viewMonitoring(ActionEvent e) {
        loadView("admin/system_monitoring.fxml");
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
        alert.setHeaderText("MedEase Admin Dashboard");
        alert.setContentText(
                "Admin workspace:\n" +
                        "• Doctor management\n" +
                        "• Appointment approval\n" +
                        "• Schedules\n" +
                        "• Patient records\n" +
                        "• Reviews and monitoring"
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
