module chatapp {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    
    opens chatapp to javafx.fxml;
    exports chatapp;
}
