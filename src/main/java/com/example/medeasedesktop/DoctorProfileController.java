package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.PatientDoctorDAO;
import com.example.medeasedesktop.dao.ReviewsDAO;
import com.example.medeasedesktop.model.DoctorProfileRow;
import com.example.medeasedesktop.model.ReviewRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class DoctorProfileController {

    @FXML private Label nameLabel;
    @FXML private Label specialtyLabel;
    @FXML private Label aboutLabel;

    @FXML private TableView<ReviewRow> reviewTable;
    @FXML private TableColumn<ReviewRow,String> patientCol;
    @FXML private TableColumn<ReviewRow,String> ratingCol;
    @FXML private TableColumn<ReviewRow,String> commentCol;
    @FXML private TableColumn<ReviewRow,String> dateCol;

    private final PatientDoctorDAO doctorDAO = new PatientDoctorDAO();
    private final ReviewsDAO reviewsDAO = new ReviewsDAO();

    private int doctorId;

    public void loadDoctor(int doctorId) {
        this.doctorId = doctorId;

        DoctorProfileRow d = doctorDAO.getDoctorProfile(doctorId);
        if (d == null) {
            nameLabel.setText("Doctor not found");
            specialtyLabel.setText("");
            aboutLabel.setText("");
            reviewTable.setItems(FXCollections.observableArrayList());
            return;
        }

        nameLabel.setText(d.getName());
        specialtyLabel.setText(d.getSpecialty());
        aboutLabel.setText(d.getAbout());

        patientCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPatientName()));
        ratingCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getRating())));
        commentCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getComment())));
        dateCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getCreatedAt())));

        reviewTable.setItems(FXCollections.observableArrayList(
                reviewsDAO.getReviews(doctorId, null)
        ));
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
