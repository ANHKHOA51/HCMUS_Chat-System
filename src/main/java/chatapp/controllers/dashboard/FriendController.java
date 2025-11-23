package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

public class FriendController extends DashboardController {

    @FXML
    private TableColumn<?, ?> emailCol;

    @FXML
    private TableColumn<?, ?> friendCol;

    @FXML
    private TableColumn<?, ?> friendOfFriendCol;

    @FXML
    private TableColumn<?, ?> userNameCol;

    @FXML
    private TableColumn<?, ?> nameCol;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

    }

}