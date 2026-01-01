package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.PatientRecordsDAO;
import com.example.medeasedesktop.model.PatientRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class PatientRecordsController {

    @FXML private TextField searchIdField;
    @FXML private TextField searchNameField;
    @FXML private TextField searchPhoneField;

    @FXML private TableView<PatientRow> patientTable;
    @FXML private TableColumn<PatientRow, Integer> idCol;
    @FXML private TableColumn<PatientRow, String> nameCol;
    @FXML private TableColumn<PatientRow, String> phoneCol;
    @FXML private TableColumn<PatientRow, String> genderCol;
    @FXML private TableColumn<PatientRow, Integer> ageCol;

    private final PatientRecordsDAO dao = new PatientRecordsDAO();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("fullName"));
        phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));
        genderCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("gender"));
        ageCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("age"));

        searchIdField.textProperty().addListener((a,b,c) -> refresh());
        searchNameField.textProperty().addListener((a,b,c) -> refresh());
        searchPhoneField.textProperty().addListener((a,b,c) -> refresh());

        refresh();
    }

    @FXML
    void search() {
        refresh();
    }

    @FXML
    void refresh() {
        patientTable.setItems(FXCollections.observableArrayList(
                dao.listPatients(
                        searchIdField.getText(),
                        searchNameField.getText(),
                        searchPhoneField.getText()
                )
        ));
    }

    @FXML
    void addPatient() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Patient");

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField fullName = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        TextField dob = new TextField();
        TextField gender = new TextField();
        TextArea address = new TextArea();
        address.setPrefRowCount(3);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Full Name*"), 0, 0); gp.add(fullName, 1, 0);
        gp.add(new Label("Email"), 0, 1); gp.add(email, 1, 1);
        gp.add(new Label("Phone"), 0, 2); gp.add(phone, 1, 2);
        gp.add(new Label("DOB (YYYY-MM-DD)"), 0, 3); gp.add(dob, 1, 3);
        gp.add(new Label("Gender"), 0, 4); gp.add(gender, 1, 4);
        gp.add(new Label("Address"), 0, 5); gp.add(address, 1, 5);

        dialog.getDialogPane().setContent(gp);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != addBtn) return;

            String n = fullName.getText() == null ? "" : fullName.getText().trim();
            if (n.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "Full Name is required.").show();
                return;
            }

            String e = email.getText() == null ? "" : email.getText().trim();
            if (!e.isBlank() && !e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
                new Alert(Alert.AlertType.WARNING, "Enter a valid email (or leave blank).").show();
                return;
            }

            String d = dob.getText() == null ? "" : dob.getText().trim();
            if (!d.isBlank() && !d.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
                new Alert(Alert.AlertType.WARNING, "DOB format must be YYYY-MM-DD (or leave blank).").show();
                return;
            }

            boolean ok = dao.addPatient(
                    n,
                    e,
                    phone.getText(),
                    d,
                    gender.getText(),
                    address.getText()
            );

            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "Failed to add patient.").show();
                return;
            }

            refresh();
        });
    }

    @FXML
    void viewDetails() {
        PatientRow row = patientTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a patient first.").show();
            return;
        }

        PatientRecordsDAO.PatientDetails d = dao.getPatientDetails(row.getId());
        if (d == null) {
            new Alert(Alert.AlertType.ERROR, "Patient not found.").show();
            return;
        }

        String msg = ""
                + "ID: " + d.id() + "\n"
                + "Name: " + safe(d.fullName()) + "\n"
                + "Email: " + safe(d.email()) + "\n"
                + "Phone: " + safe(d.phone()) + "\n"
                + "DOB: " + safe(d.dob()) + "\n"
                + "Gender: " + safe(d.gender()) + "\n"
                + "Address: " + safe(d.address());

        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Patient Details");
        info.setHeaderText("Patient #" + d.id());
        info.setContentText(msg);
        info.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        info.show();
    }

    @FXML
    void deleteSelected() {
        PatientRow row = patientTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a patient first.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete patient \"" + row.getFullName() + "\"?",
                ButtonType.YES, ButtonType.NO);

        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;

            boolean ok = dao.deletePatient(row.getId());
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "Failed to delete patient.").show();
                return;
            }
            refresh();
        });
    }

    private String safe(String s) {
        return (s == null || s.isBlank()) ? "-" : s;
    }
}
