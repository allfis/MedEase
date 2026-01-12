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

import java.net.URL;

public class PatientDashboardController implements PatientNavigator {

    @FXML private BorderPane contentHolder;
    @FXML private Label patientNameLabel;

    @FXML
    public void initialize() {
        patientNameLabel.setText(
                (Session.getFullName() == null || Session.getFullName().isBlank())
                        ? "Patient"
                        : Session.getFullName()
        );
        loadView("patient/doctor_search.fxml");
    }

    @FXML
    void showSearch(ActionEvent e) {
        loadView("patient/doctor_search.fxml");
    }



    @FXML
    void showMyAppointments(ActionEvent e) {
        loadView("patient/my_appointments.fxml");
    }

    @Override
    public void openBookAppointment(int doctorId) {
        loadViewWithDoctor("patient/book_appointment.fxml", doctorId);
    }

    private void loadView(String relPath) {
        try {
            String absPath = "/com/example/medeasedesktop/" + relPath;
            URL url = getClass().getResource(absPath);

            if (url == null) {
                contentHolder.setCenter(new Label("FXML NOT FOUND:\n" + absPath));
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof PatientNavAware navAware) {
                navAware.setNavigator(this);
            }

            contentHolder.setCenter(view);

        } catch (Exception ex) {
            ex.printStackTrace();
            Label err = new Label("FAILED TO LOAD VIEW\n\n" + ex.getMessage());
            err.setWrapText(true);
            contentHolder.setCenter(err);
        }
    }

    private void loadViewWithDoctor(String relPath, int doctorId) {
        try {
            String absPath = "/com/example/medeasedesktop/" + relPath;
            URL url = getClass().getResource(absPath);

            if (url == null) {
                contentHolder.setCenter(new Label("FXML NOT FOUND:\n" + absPath));
                return;
            }

            FXMLLoader loader = new FXMLLoader(url);
            Node view = loader.load();

            Object controller = loader.getController();
            if (controller instanceof com.example.medeasedesktop.patient.BookAppointmentController c) {
                c.setDoctorId(doctorId);
            }
            if (controller instanceof PatientNavAware navAware) {
                navAware.setNavigator(this);
            }

            contentHolder.setCenter(view);

        } catch (Exception ex) {
            ex.printStackTrace();
            Label err = new Label("FAILED TO LOAD VIEW\n\n" + ex.getMessage());
            err.setWrapText(true);
            contentHolder.setCenter(err);
        }
    }

    @FXML
    void handleLogout(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/example/medeasedesktop/login.fxml"));
            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to logout.").show();
        }
    }

    @FXML
    void handleExit(ActionEvent event) {
        System.exit(0);
    }

    private Stage getStageFromEvent(ActionEvent event) {
        Object src = event.getSource();
        if (src instanceof MenuItem menuItem) {
            return (Stage) menuItem.getParentPopup().getOwnerWindow();
        }
        return (Stage) ((Node) src).getScene().getWindow();
    }
}
