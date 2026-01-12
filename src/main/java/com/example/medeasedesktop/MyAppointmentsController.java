package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.PatientAppointmentsDAO;
import com.example.medeasedesktop.model.PatientAppointmentRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MyAppointmentsController {

    @FXML private Label statusLabel;

    @FXML private TableView<PatientAppointmentRow> table;
    @FXML private TableColumn<PatientAppointmentRow, String> dateCol;
    @FXML private TableColumn<PatientAppointmentRow, String> timeCol;
    @FXML private TableColumn<PatientAppointmentRow, String> doctorCol;
    @FXML private TableColumn<PatientAppointmentRow, String> statusCol;

    private final PatientAppointmentsDAO dao = new PatientAppointmentsDAO();

    @FXML
    public void initialize() {
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getDate())));
        timeCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getTime())));
        doctorCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getDoctor())));
        statusCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getStatus())));
        refresh();
    }

    @FXML
    public void refresh() {
        int userId = Session.getUserId();
        var rows = dao.listMyAppointments(userId);
        table.setItems(FXCollections.observableArrayList(rows));
        statusLabel.setText("Total: " + rows.size());
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
