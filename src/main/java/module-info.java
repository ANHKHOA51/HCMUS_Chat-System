module chatapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens chatapp to javafx.fxml;
    opens chatapp.controllers.dashboard to javafx.fxml;
    opens chatapp.models to javafx.base;

    exports chatapp;
}
