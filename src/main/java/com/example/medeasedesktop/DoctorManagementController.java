package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.DoctorManagementDAO;
import com.example.medeasedesktop.model.DoctorRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

public class DoctorManagementController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;

    @FXML private TableView<DoctorRow> doctorTable;
    @FXML private TableColumn<DoctorRow, Integer> idCol;
    @FXML private TableColumn<DoctorRow, String> nameCol;
    @FXML private TableColumn<DoctorRow, String> emailCol;
    @FXML private TableColumn<DoctorRow, String> statusCol;

    private final DoctorManagementDAO dao = new DoctorManagementDAO();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        statusFilter.setItems(FXCollections.observableArrayList("All", "Enabled", "Disabled"));
        statusFilter.getSelectionModel().select("All");

        searchField.textProperty().addListener((a,b,c) -> refresh());
        statusFilter.valueProperty().addListener((a,b,c) -> refresh());

        refresh();
    }

    @FXML
    void refresh() {
        doctorTable.setItems(FXCollections.observableArrayList(
                dao.listDoctors(searchField.getText(), statusFilter.getValue())
        ));
    }

    @FXML
    void addDoctor() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Doctor");

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        TextField name = new TextField();
        TextField email = new TextField();
        PasswordField pass = new PasswordField();

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);

        gp.add(new Label("Full Name"), 0, 0);
        gp.add(name, 1, 0);

        gp.add(new Label("Email"), 0, 1);
        gp.add(email, 1, 1);

        gp.add(new Label("Password"), 0, 2);
        gp.add(pass, 1, 2);

        dialog.getDialogPane().setContent(gp);

        dialog.showAndWait().ifPresent(bt -> {
            if (bt != addBtn) return;

            String n = name.getText() == null ? "" : name.getText().trim();
            String e = email.getText() == null ? "" : email.getText().trim();
            String p = pass.getText() == null ? "" : pass.getText();

            if (n.isBlank() || e.isBlank() || p.isBlank()) {
                new Alert(Alert.AlertType.WARNING, "All fields are required.").show();
                return;
            }

            if (!e.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)+$")) {
                new Alert(Alert.AlertType.WARNING, "Enter a valid email address.").show();
                return;
            }

            boolean ok = dao.addDoctor(n, e, p);
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "Failed to add doctor (email may already exist).").show();
                return;
            }

            refresh();
        });
    }

    @FXML
    void enableSelected() {
        DoctorRow row = doctorTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a doctor first.").show();
            return;
        }
        if (!dao.setEnabled(row.getId(), true)) {
            new Alert(Alert.AlertType.ERROR, "Failed to enable doctor.").show();
            return;
        }
        refresh();
    }

    @FXML
    void disableSelected() {
        DoctorRow row = doctorTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a doctor first.").show();
            return;
        }
        if (!dao.setEnabled(row.getId(), false)) {
            new Alert(Alert.AlertType.ERROR, "Failed to disable doctor.").show();
            return;
        }
        refresh();
    }

    @FXML
    void removeSelected() {
        DoctorRow row = doctorTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a doctor first.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Remove this doctor?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;

            boolean ok = dao.removeDoctor(row.getId());
            if (!ok) {
                new Alert(Alert.AlertType.ERROR, "Failed to remove doctor.").show();
                return;
            }
            refresh();
        });
    }
}
