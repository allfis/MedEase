package com.example.medeasedesktop;

import com.example.medeasedesktop.Session;
import com.example.medeasedesktop.dao.ReviewsDAO;
import com.example.medeasedesktop.model.ReviewRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ReviewsController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> ratingFilter;

    @FXML private TableView<ReviewRow> reviewsTable;
    @FXML private TableColumn<ReviewRow, Integer> idCol;
    @FXML private TableColumn<ReviewRow, String> patientCol;
    @FXML private TableColumn<ReviewRow, String> doctorCol;
    @FXML private TableColumn<ReviewRow, Integer> ratingCol;
    @FXML private TableColumn<ReviewRow, String> commentCol;
    @FXML private TableColumn<ReviewRow, String> dateCol;

    private final ReviewsDAO dao = new ReviewsDAO();

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        patientCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("patient"));
        doctorCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("doctor"));
        ratingCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("rating"));
        commentCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("comment"));
        dateCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("date"));

        ratingFilter.setItems(FXCollections.observableArrayList("All", "1", "2", "3", "4", "5"));
        ratingFilter.getSelectionModel().select("All");

        searchField.textProperty().addListener((a,b,c) -> refresh());
        ratingFilter.valueProperty().addListener((a,b,c) -> refresh());

        refresh();
    }

    @FXML
    void refresh() {
        Integer r = null;
        String v = ratingFilter.getValue();
        if (v != null && !"All".equalsIgnoreCase(v)) {
            try { r = Integer.parseInt(v); } catch (Exception ignored) {}
        }

        reviewsTable.setItems(FXCollections.observableArrayList(
                dao.list(searchField.getText(), r)
        ));
    }

    @FXML
    void deleteSelected() {
        if (!"ADMIN".equalsIgnoreCase(Session.getRole())) {
            new Alert(Alert.AlertType.WARNING, "Only Admin can delete reviews.").show();
            return;
        }

        ReviewRow row = reviewsTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            new Alert(Alert.AlertType.WARNING, "Select a review first.").show();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Delete this review?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.YES) return;

            boolean ok = dao.deleteReview(row.getId(), Session.getRole(), Session.getEmail());
            if (!ok) new Alert(Alert.AlertType.ERROR, "Failed to delete review.").show();
            refresh();
        });
    }
}
