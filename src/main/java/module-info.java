module com.example.universitytest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.universitytest.controllers to javafx.fxml; // Добавьте эту строку

    exports com.example.universitytest;
}
