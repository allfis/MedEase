package com.example.medeasedesktop.patient;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.dao.PatientAppointmentsDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;

public class BookAppointmentController {

    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private Label statusLabel;

    private final PatientAppointmentsDAO dao = new PatientAppointmentsDAO();
    private int doctorId;

    @FXML
    public void initialize() {
        if (datePicker != null) datePicker.setValue(LocalDate.now().plusDays(1));
        if (statusLabel != null) statusLabel.setText("");
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    @FXML
    public void book() {
        if (doctorId <= 0) {
            showError("Doctor not selected.");
            return;
        }

        int patientId = Session.getPatientId();
        if (patientId <= 0) {
            showError("Patient session not set.");
            return;
        }

        if (datePicker.getValue() == null) {
            showError("Select a date.");
            return;
        }

        String time = timeField.getText() == null ? "" : timeField.getText().trim();
        String t = normalizeTime(time);
        if (t == null) {
            showError("Time format: HH:mm (example 10:30)");
            return;
        }

        boolean ok = dao.book(patientId, doctorId, datePicker.getValue().toString(), t);

        if (ok) {
            if (statusLabel != null) statusLabel.setText("Appointment requested");
            new Alert(Alert.AlertType.INFORMATION, "Appointment requested successfully.").showAndWait();
        } else {
            if (statusLabel != null) statusLabel.setText("Failed");
            showError("Failed to request appointment.");
        }
    }

    private String normalizeTime(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.matches("\\d{2}:\\d{2}")) return s;
        if (s.matches("\\d{1}:\\d{2}")) return "0" + s;
        return null;
    }

    private void showError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }
}
