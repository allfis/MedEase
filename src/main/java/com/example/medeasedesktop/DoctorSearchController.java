package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.PatientDoctorDAO;
import com.example.medeasedesktop.model.PatientDoctorRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class DoctorSearchController implements PatientNavAware {

    @FXML private TextField queryField;
    @FXML private TableView<PatientDoctorRow> doctorTable;

    @FXML private TableColumn<PatientDoctorRow, String> nameCol;
    @FXML private TableColumn<PatientDoctorRow, String> emailCol;
    @FXML private TableColumn<PatientDoctorRow, String> specialtyCol;
    @FXML private TableColumn<PatientDoctorRow, String> hospitalCol;

    @FXML private Label statusLabel;

    private final PatientDoctorDAO dao = new PatientDoctorDAO();
    private PatientNavigator navigator;

    @Override
    public void setNavigator(PatientNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        hospitalCol.setCellValueFactory(new PropertyValueFactory<>("hospital"));
        load("");
    }

    @FXML
    void handleSearch() {
        load(queryField.getText());
    }

    @FXML
    void handleClear() {
        queryField.setText("");
        load("");
    }

    @FXML
    void openProfile() {
        PatientDoctorRow row = doctorTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            statusLabel.setText("Select a doctor first.");
            return;
        }
        if (navigator == null) {
            statusLabel.setText("Navigation not ready.");
            return;
        }
        navigator.openBookAppointment(row.getDoctorId());
    }

    private void load(String q) {
        var list = dao.searchDoctors(q);
        doctorTable.setItems(FXCollections.observableArrayList(list));
        statusLabel.setText("Found: " + list.size());
    }
}
