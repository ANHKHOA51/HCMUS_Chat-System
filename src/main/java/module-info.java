module chatapp {
    requires javafx.controls;
    requires javafx.fxml;

    opens chatapp to javafx.fxml;
    exports chatapp;
}
