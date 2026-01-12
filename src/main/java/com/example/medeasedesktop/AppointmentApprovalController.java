package com.example.medeasedesktop;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.dao.AppointmentsDAO;
import com.example.medeasedesktop.model.AppointmentRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class AppointmentApprovalController {

    @FXML private TableView<AppointmentRow> table;
    @FXML private TableColumn<AppointmentRow, Integer> idCol;
    @FXML private TableColumn<AppointmentRow, String> patientCol;
    @FXML private TableColumn<AppointmentRow, String> dateCol;
    @FXML private TableColumn<AppointmentRow, String> timeCol;
    @FXML private TableColumn<AppointmentRow, String> doctorCol;
    @FXML private TableColumn<AppointmentRow, String> statusCol;

    @FXML private ComboBox<AppointmentsDAO.DoctorOption> doctorCombo;
    @FXML private Label statusLabel;

    private final AppointmentsDAO dao = new AppointmentsDAO();

    @FXML
    public void initialize() {
        // table binding
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patient"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("apptDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("apptTime"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctor"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadDoctors();
        refresh(null);
    }

    private void loadDoctors() {
        List<AppointmentsDAO.DoctorOption> docs = dao.listEnabledDoctors();
        doctorCombo.setItems(FXCollections.observableArrayList(docs));
        if (!docs.isEmpty()) doctorCombo.getSelectionModel().select(0);
    }

    @FXML
    public void refresh(javafx.event.ActionEvent e) {
        List<AppointmentRow> rows = dao.listForAdmin();
        table.setItems(FXCollections.observableArrayList(rows));
        statusLabel.setText("Loaded " + rows.size() + " appointments");
    }

    @FXML
    public void approveSelected() {
        AppointmentRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Select an appointment first."); return; }

        boolean ok = dao.approve(selected.getId(), Session.getRole(), Session.getEmail());
        statusLabel.setText(ok ? "Approved #" + selected.getId() : "Approve failed");
        refresh(null);
    }

    @FXML
    public void rejectSelected() {
        AppointmentRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Select an appointment first."); return; }

        boolean ok = dao.reject(selected.getId(), Session.getRole(), Session.getEmail());
        statusLabel.setText(ok ? "Rejected #" + selected.getId() : "Reject failed");
        refresh(null);
    }

    @FXML
    public void assignDoctorToSelected() {
        AppointmentRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Select an appointment first."); return; }

        AppointmentsDAO.DoctorOption doc = doctorCombo.getValue();
        if (doc == null) { warn("Select a doctor to assign."); return; }

        boolean ok = dao.assignDoctor(selected.getId(), doc.id(), Session.getRole(), Session.getEmail());
        statusLabel.setText(ok ? "Assigned doctor to #" + selected.getId() : "Assign failed");
        refresh(null);
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}
