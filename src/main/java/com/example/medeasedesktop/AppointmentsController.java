package com.example.medeasedesktop;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.dao.AppointmentsDAO;
import com.example.medeasedesktop.model.AppointmentRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AppointmentsController {

    @FXML private TableView<AppointmentRow> table;
    @FXML private TableColumn<AppointmentRow, String> patientCol;
    @FXML private TableColumn<AppointmentRow, String> dateCol;
    @FXML private TableColumn<AppointmentRow, String> timeCol;
    @FXML private TableColumn<AppointmentRow, String> doctorCol;
    @FXML private TableColumn<AppointmentRow, String> statusCol;

    private final AppointmentsDAO dao = new AppointmentsDAO();

    @FXML
    public void initialize() {
        patientCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("patient"));
        dateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("time"));
        doctorCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("doctor"));
        statusCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        refresh();
    }

    private void refresh() {
        String role = Session.getRole() == null ? "" : Session.getRole();
        List<AppointmentRow> rows = "DOCTOR".equalsIgnoreCase(role)
                ? dao.listForDoctor(Session.getUserId())
                : dao.listForAdmin();
        table.setItems(FXCollections.observableArrayList(rows));
    }

    @FXML
    void approveSelected() {
        AppointmentRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select an appointment first.").show();
            return;
        }
        boolean ok = dao.approve(row.getId(), Session.getRole(), Session.getEmail());
        if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to approve.").show();
        refresh();
    }

    @FXML
    void rejectSelected() {
        AppointmentRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select an appointment first.").show();
            return;
        }
        boolean ok = dao.reject(row.getId(), Session.getRole(), Session.getEmail());
        if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to reject.").show();
        refresh();
    }

    @FXML
    void assignDoctorSelected() {
        AppointmentRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select an appointment first.").show();
            return;
        }

        if ("DOCTOR".equalsIgnoreCase(Session.getRole())) {
            new Alert(Alert.AlertType.WARNING, "Only Admin can assign doctors.").show();
            return;
        }

        List<AppointmentsDAO.DoctorOption> doctors = dao.listEnabledDoctors();
        if (doctors.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "No enabled doctors found.").show();
            return;
        }

        ChoiceDialog<AppointmentsDAO.DoctorOption> dialog = new ChoiceDialog<>(doctors.get(0), doctors);
        dialog.setTitle("Assign Doctor");
        dialog.setHeaderText("Assign a doctor to appointment #" + row.getId());
        dialog.setContentText("Select Doctor:");

        dialog.showAndWait().ifPresent(doc -> {
            boolean ok = dao.assignDoctor(row.getId(), doc.id(), Session.getRole(), Session.getEmail());
            if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to assign doctor.").show();
            refresh();
        });
    }

    @FXML
    void markCompletedSelected() {
        AppointmentRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select an appointment first.").show();
            return;
        }
        boolean ok = dao.markCompleted(row.getId(), Session.getRole(), Session.getEmail());
        if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to mark completed.").show();
        refresh();
    }

    @FXML
    void requestRescheduleSelected() {
        AppointmentRow row = table.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select an appointment first.").show();
            return;
        }
        boolean ok = dao.requestReschedule(row.getId(), Session.getRole(), Session.getEmail());
        if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to request reschedule.").show();
        refresh();
    }
}
