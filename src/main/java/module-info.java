module com.example.medeasedesktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.xerial.sqlitejdbc;
    requires jbcrypt;

    exports com.example.medeasedesktop;

    opens com.example.medeasedesktop to javafx.fxml;
    opens com.example.medeasedesktop.dao to javafx.fxml;
    opens com.example.medeasedesktop.model to javafx.base, javafx.fxml;
    opens com.example.medeasedesktop.db to javafx.fxml;
}
