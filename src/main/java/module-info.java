module com.example.javafx_reactivo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens com.example.javafx_reactivo.modelos to com.fasterxml.jackson.databind, javafx.base;
    opens com.example.javafx_reactivo to javafx.fxml;
    exports com.example.javafx_reactivo;
}