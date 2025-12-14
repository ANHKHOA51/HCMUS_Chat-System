module chatapp {
    requires transitive javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires io.github.cdimascio.dotenv.java;
    requires org.java_websocket;
    requires java.mail;
    requires java.net.http;
    requires org.json;

    opens chatapp to javafx.fxml;
    opens chatapp.controllers.dashboard to javafx.fxml;
    opens chatapp.models to javafx.base;
    opens chatapp.dto to javafx.base;

    exports chatapp;
}
