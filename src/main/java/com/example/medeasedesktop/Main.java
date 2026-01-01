package com.example.medeasedesktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.scene.Parent;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        com.example.medeasedesktop.db.Schema.init();
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("images/MEDEASE.png")));
        primaryStage.setScene(new Scene(root, 600, 500));
        primaryStage.setTitle("MedEase");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
