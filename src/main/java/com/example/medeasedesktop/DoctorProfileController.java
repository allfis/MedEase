package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.DoctorProfileDAO;
import com.example.medeasedesktop.Session;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class DoctorProfileController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField specialtyField;
    @FXML private TextArea aboutArea;

    private final DoctorProfileDAO dao = new DoctorProfileDAO();

    @FXML
    public void initialize() {
        DoctorProfileDAO.Profile p = dao.getProfile(Session.getUserId());

        if (p != null) {
            nameField.setText(p.fullName);
            phoneField.setText(p.phone);
            specialtyField.setText(p.specialty);
            aboutArea.setText(p.about);
        }
    }
}
