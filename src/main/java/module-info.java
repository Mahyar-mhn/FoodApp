module org.example.foodapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens org.example.foodapp to javafx.fxml;
    exports org.example.foodapp;
    exports Classes;
    opens Classes to javafx.fxml;
}