package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.SystemMonitoringDAO;
import com.example.medeasedesktop.model.ActivityRow;
import com.example.medeasedesktop.model.SystemStats;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class SystemMonitoringController {

    @FXML private Label todayAppointmentsLabel;
    @FXML private Label approvedLabel;
    @FXML private Label rejectedCancelledLabel;
    @FXML private Label activeDoctorsLabel;
    @FXML private Label onLeaveLabel;
    @FXML private Label newReviewsLabel;

    @FXML private Label statusLabel;

    @FXML private TableView<ActivityRow> activityTable;
    @FXML private TableColumn<ActivityRow, String> timeCol;
    @FXML private TableColumn<ActivityRow, String> eventCol;
    @FXML private TableColumn<ActivityRow, String> userCol;
    @FXML private TableColumn<ActivityRow, String> detailsCol;

    private final SystemMonitoringDAO dao = new SystemMonitoringDAO();

    @FXML
    public void initialize() {
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        eventCol.setCellValueFactory(new PropertyValueFactory<>("event"));
        userCol.setCellValueFactory(new PropertyValueFactory<>("user"));
        detailsCol.setCellValueFactory(new PropertyValueFactory<>("details"));
        refresh();
    }

    @FXML
    public void refresh() {
        SystemStats s = dao.loadStats();
        todayAppointmentsLabel.setText(String.valueOf(s.getTodayAppointments()));
        approvedLabel.setText(String.valueOf(s.getApproved()));
        rejectedCancelledLabel.setText(String.valueOf(s.getRejectedOrCancelled()));
        activeDoctorsLabel.setText(String.valueOf(s.getActiveDoctors()));
        onLeaveLabel.setText(String.valueOf(s.getOnLeaveToday()));
        newReviewsLabel.setText(String.valueOf(s.getNewReviewsToday()));
        activityTable.setItems(FXCollections.observableArrayList(dao.recentActivity(30)));
        statusLabel.setText("Last updated: " + s.getGeneratedAt());
    }
}
