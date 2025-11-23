package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class ActivityController extends DashboardController {

    @FXML
    private TableColumn<?, ?> ActivityCol;

    @FXML
    private TableColumn<?, ?> TimeCol;

    @FXML
    private TableColumn<?, ?> UserNameCol;

    @FXML
    private TableColumn<?, ?> userAccountCol;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}
