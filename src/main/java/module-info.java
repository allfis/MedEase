
module com.example.medeasedesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jbcrypt;

    opens com.example.medeasedesktop.model to javafx.base;
    opens com.example.medeasedesktop to javafx.fxml;
    exports com.example.medeasedesktop;
}