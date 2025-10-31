package chatapp.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;

public class DashboardChatGroup implements Initializable {

    @FXML
    private TableColumn<?, ?> adminEmailCol;

    @FXML
    private TableColumn<?, ?> adminGenderCol;

    @FXML
    private TableColumn<?, ?> adminNameCol;

    @FXML
    private TableColumn<?, ?> groupCreatedAtCol;

    @FXML
    private TableColumn<?, ?> groupMemberCol;

    @FXML
    private TableColumn<?, ?> groupNameCol;

    @FXML
    private TableColumn<?, ?> memberEmailCol;

    @FXML
    private TableColumn<?, ?> memberGenderCol;

    @FXML
    private TableColumn<?, ?> memberNamecol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

}
