package chatapp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;

public class DashboardRegistration implements Initializable {

    @FXML
    private TableColumn<?, ?> emailCol;

    @FXML
    private TableColumn<?, ?> timeCol;

    @FXML
    private TableColumn<?, ?> userAccountCol;

    @FXML
    private TableColumn<?, ?> userNameCol;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
