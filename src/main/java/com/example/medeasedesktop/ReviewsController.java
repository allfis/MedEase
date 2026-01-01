package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.ReviewsDAO;
import com.example.medeasedesktop.model.ReviewRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class ReviewsController {

    @FXML private ComboBox<ReviewsDAO.DoctorItem> doctorCombo;
    @FXML private ComboBox<Integer> minRatingCombo;

    @FXML private TableView<ReviewRow> table;
    @FXML private TableColumn<ReviewRow, String> colDoctor;
    @FXML private TableColumn<ReviewRow, String> colPatient;
    @FXML private TableColumn<ReviewRow, String> colRating;
    @FXML private TableColumn<ReviewRow, String> colComment;
    @FXML private TableColumn<ReviewRow, String> colCreated;

    private final ReviewsDAO dao = new ReviewsDAO();

    @FXML
    public void initialize() {

        minRatingCombo.setItems(FXCollections.observableArrayList(1, 2, 3, 4, 5));
        minRatingCombo.getSelectionModel().select(Integer.valueOf(1));

        colDoctor.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDoctorName()));
        colPatient.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getPatientName()));
        colRating.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getRating())));
        colComment.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(safe(c.getValue().getComment())));
        colCreated.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCreatedAt()));

        loadDoctors();
        refresh(null, null);
    }

    @FXML
    private void onFilter() {
        Integer doctorId = null;
        ReviewsDAO.DoctorItem doc = doctorCombo.getValue();
        if (doc != null && doc.id != -1) doctorId = doc.id;

        Integer minRating = minRatingCombo.getValue();
        refresh(doctorId, minRating);
    }

    @FXML
    private void onReset() {
        if (doctorCombo.getItems() != null && !doctorCombo.getItems().isEmpty()) {
            doctorCombo.getSelectionModel().select(0);
        }
        minRatingCombo.getSelectionModel().select(Integer.valueOf(1));
        refresh(null, null);
    }

    @FXML
    private void onRefresh() {
        onFilter();
    }

    @FXML
    private void onDeleteSelected() {
        ReviewRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            warn("Select a review to delete.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Delete this review?\nThis cannot be undone.",
                ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);

        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        dao.deleteReview(selected.getId());
        onFilter();
    }

    private void loadDoctors() {
        List<ReviewsDAO.DoctorItem> doctors = dao.getDoctors();
        doctors.add(0, new ReviewsDAO.DoctorItem(-1, "All Doctors"));
        doctorCombo.setItems(FXCollections.observableArrayList(doctors));
        doctorCombo.getSelectionModel().select(0);
    }

    private void refresh(Integer doctorId, Integer minRating) {
        List<ReviewRow> rows = dao.getReviews(doctorId, minRating);
        table.setItems(FXCollections.observableArrayList(rows));
    }

    private String safe(String s) {
        return (s == null) ? "" : s;
    }

    private void warn(String msg) {
        new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK).showAndWait();
    }
}
