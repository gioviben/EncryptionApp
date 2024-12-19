module com.example.encryption2 {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.encryption2 to javafx.fxml;
    exports com.example.encryption2;
    exports com.example.encryption2.progressbar;
    opens com.example.encryption2.progressbar to javafx.fxml;
    exports com.example.encryption2.encryption;
    opens com.example.encryption2.encryption to javafx.fxml;

}