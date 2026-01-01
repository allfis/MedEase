package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.ScheduleManagementDAO;
import com.example.medeasedesktop.model.ScheduleRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ScheduleManagementController {

    @FXML private ComboBox<ScheduleManagementDAO.DoctorItem> doctorCombo;
    @FXML private ComboBox<String> dayCombo;
    @FXML private ComboBox<String> typeCombo;

    @FXML private TextField startField;
    @FXML private TextField endField;

    @FXML private TableView<ScheduleRow> table;
    @FXML private TableColumn<ScheduleRow, String> colDoctor;
    @FXML private TableColumn<ScheduleRow, String> colDay;
    @FXML private TableColumn<ScheduleRow, String> colStart;
    @FXML private TableColumn<ScheduleRow, String> colEnd;
    @FXML private TableColumn<ScheduleRow, String> colType;
    @FXML private TableColumn<ScheduleRow, String> colCreated;

    private final ScheduleManagementDAO dao = new ScheduleManagementDAO();

    @FXML
    public void initialize() {

        dayCombo.setItems(FXCollections.observableArrayList(
                "SATURDAY", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"
        ));
        dayCombo.getSelectionModel().select("SUNDAY");

        typeCombo.setItems(FXCollections.observableArrayList("WORKING", "LEAVE", "HOLIDAY"));
        typeCombo.getSelectionModel().select("WORKING");

        colDoctor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDoctorName()));
        colDay.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDay()));
        colStart.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getStartTime()));
        colEnd.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getEndTime()));
        colType.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getType()));
        colCreated.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt()));

        loadDoctors();
        refresh();

        table.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel == null) return;
            dayCombo.getSelectionModel().select(sel.getDay());
            typeCombo.getSelectionModel().select(sel.getType());
            startField.setText(sel.getStartTime());
            endField.setText(sel.getEndTime());
        });
    }

    @FXML
    private void onAdd() {
        ScheduleManagementDAO.DoctorItem doc = doctorCombo.getValue();
        if (doc == null) { warn("Select doctor."); return; }

        String day = dayCombo.getValue();
        String type = typeCombo.getValue();
        String start = startField.getText().trim();
        String end = endField.getText().trim();

        if (day == null || type == null || start.isBlank() || end.isBlank()) {
            warn("Fill all fields.");
            return;
        }
        if (!isTimeHHmm(start) || !isTimeHHmm(end)) {
            warn("Time must be HH:mm (example 09:30).");
            return;
        }

        dao.insertSchedule(doc.id, day, start, end, type);
        startField.clear();
        endField.clear();
        refresh();
    }

    @FXML
    private void onUpdateSelected() {
        ScheduleRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Select a row to update."); return; }

        String day = dayCombo.getValue();
        String type = typeCombo.getValue();
        String start = startField.getText().trim();
        String end = endField.getText().trim();

        if (day == null || type == null || start.isBlank() || end.isBlank()) {
            warn("Fill all fields.");
            return;
        }
        if (!isTimeHHmm(start) || !isTimeHHmm(end)) {
            warn("Time must be HH:mm.");
            return;
        }

        dao.updateSchedule(selected.getId(), day, start, end, type);
        refresh();
    }

    @FXML
    private void onDeleteSelected() {
        ScheduleRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) { warn("Select a row to delete."); return; }
        dao.deleteSchedule(selected.getId());
        refresh();
    }

    @FXML
    private void onRefresh() {
        refresh();
    }

    private void loadDoctors() {
        List<ScheduleManagementDAO.DoctorItem> doctors = dao.getDoctors();
        doctorCombo.setItems(FXCollections.observableArrayList(doctors));
        if (!doctors.isEmpty()) doctorCombo.getSelectionModel().select(0);
    }

    private void refresh() {
        List<ScheduleRow> rows = dao.getAllSchedules();
        table.setItems(FXCollections.observableArrayList(rows));
    }

    private boolean isTimeHHmm(String t) {
        if (t == null) return false;
        if (!t.matches("\\d{2}:\\d{2}")) return false;
        int hh = Integer.parseInt(t.substring(0, 2));
        int mm = Integer.parseInt(t.substring(3, 5));
        return hh >= 0 && hh <= 23 && mm >= 0 && mm <= 59;
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}
