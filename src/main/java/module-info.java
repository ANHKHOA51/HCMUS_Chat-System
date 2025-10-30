module chatapp {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    
    opens chatapp to javafx.fxml;
    exports chatapp;
}
