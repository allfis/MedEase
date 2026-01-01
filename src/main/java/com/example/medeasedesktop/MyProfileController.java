package com.example.medeasedesktop;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.dao.DoctorProfileDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class MyProfileController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField specialtyField;
    @FXML private TextArea aboutArea;

    private final DoctorProfileDAO dao = new DoctorProfileDAO();

    @FXML
    public void initialize() {
        var p = dao.getProfile(Session.getUserId());
        if (p != null) {
            nameField.setText(p.fullName);
            phoneField.setText(p.phone);
            specialtyField.setText(p.specialty);
            aboutArea.setText(p.about);
        }
    }

    @FXML
    private void onSave() {
        dao.saveProfile(
                Session.getUserId(),
                nameField.getText(),
                phoneField.getText(),
                specialtyField.getText(),
                aboutArea.getText()
        );
        new Alert(Alert.AlertType.INFORMATION, "Profile updated").showAndWait();
    }
}
