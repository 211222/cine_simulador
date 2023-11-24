module com.example.simuladorcorte3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.simuladorcorte3 to javafx.fxml;
    exports com.example.simuladorcorte3;
}