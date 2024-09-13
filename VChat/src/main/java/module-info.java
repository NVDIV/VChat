module org.example.vchat {
    requires javafx.controls;
    requires javafx.fxml;

    // Open packages containing controllers and models for JavaFX reflection
    opens org.example.vchat to javafx.fxml;
    exports org.example.vchat;

    opens org.example.vchat.model to javafx.fxml;
    exports org.example.vchat.model;

    opens org.example.vchat.controller to javafx.fxml;
    exports org.example.vchat.controller;
}
