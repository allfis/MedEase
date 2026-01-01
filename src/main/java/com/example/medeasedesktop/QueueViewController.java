package com.example.medeasedesktop;

import com.example.medeasedesktop.dao.QueueDAO;
import com.example.medeasedesktop.model.QueueViewRow;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class QueueViewController {

    @FXML private TableView<QueueViewRow> table;
    @FXML private TableColumn<QueueViewRow, String> colTime;
    @FXML private TableColumn<QueueViewRow, String> colPatient;
    @FXML private TableColumn<QueueViewRow, String> colStatus;

    private final QueueDAO dao = new QueueDAO();

    @FXML
    public void initialize() {
        colTime.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().time));
        colPatient.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().patient));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().status));

        table.setItems(FXCollections.observableArrayList(
                dao.getTodayQueue(Session.getUserId())
        ));
    }
}
