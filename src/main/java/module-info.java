module chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens chatapp to javafx.fxml;
    opens chatapp.controller to javafx.fxml;
    opens chatapp.model to javafx.base;

    exports chatapp;
}
