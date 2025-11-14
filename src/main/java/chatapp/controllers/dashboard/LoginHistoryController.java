package chatapp.controllers.dashboard;

import java.net.URL;
import java.util.ResourceBundle;

import chatapp.models.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class LoginHistoryController extends DashboardController {

    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, String> timeCol;

    @FXML
    private TableColumn<User, String> userAccountCol;

    @FXML
    private TableColumn<User, String> userNameCol;

    private ObservableList<User> userList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        userList = FXCollections.observableArrayList(
                new User(1, "helloworld", "Hello World", "HCM", "1/1/2000", false, "helloworld@gmail.com", "online"),
                new User(2, "test", "Test", "VietNam", "2/2/2002", true, "test@gmail.com", "online"),
                new User(3, "alice", "Alice Nguyen", "Ha Noi", "3/3/2001", false, "alice@gmail.com", "offline"),
                new User(4, "bob", "Bob Tran", "Da Nang", "4/4/1999", true, "bob@gmail.com", "online"),
                new User(5, "charlie", "Charlie Le", "Can Tho", "5/5/2000", false, "charlie@gmail.com", "offline"),
                new User(6, "david", "David Pham", "Hue", "6/6/1998", true, "david@gmail.com", "offline"),
                new User(7, "eva", "Eva Vo", "Nha Trang", "7/7/2003", false, "eva@gmail.com", "online"),
                new User(8, "frank", "Frank Ngo", "Binh Duong", "8/8/2001", true, "frank@gmail.com", "offline"),
                new User(9, "grace", "Grace Truong", "Dong Nai", "9/9/2002", false, "grace@gmail.com", "online"),
                new User(10, "henry", "Henry Phan", "HCM", "10/10/2000", true, "henry@gmail.com", "offline"),
                new User(11, "ivy", "Ivy Do", "Ha Noi", "11/11/1999", false, "ivy@gmail.com", "offline"),
                new User(12, "jack", "Jack Dang", "Hue", "12/12/2001", true, "jack@gmail.com", "online"),
                new User(13, "kate", "Kate Vo", "Da Nang", "1/1/2003", false, "kate@gmail.com", "offline"),
                new User(14, "leo", "Leo Nguyen", "Can Tho", "2/2/2000", true, "leo@gmail.com", "online"),
                new User(15, "mary", "Mary Pham", "Vung Tau", "3/3/2002", false, "mary@gmail.com", "offline"),
                new User(16, "nick", "Nick Tran", "Bac Giang", "4/4/1998", true, "nick@gmail.com", "offline"),
                new User(17, "olivia", "Olivia Bui", "Da Lat", "5/5/2001", false, "olivia@gmail.com", "online"),
                new User(18, "peter", "Peter Vo", "HCM", "6/6/2002", true, "peter@gmail.com", "offline"),
                new User(19, "quinn", "Quinn Do", "Ha Noi", "7/7/1999", false, "quinn@gmail.com", "offline"),
                new User(20, "ryan", "Ryan Le", "Can Tho", "8/8/2003", true, "ryan@gmail.com", "online"));
        timeCol.setCellValueFactory(new PropertyValueFactory<User, String>("updateAt"));
        userAccountCol.setCellValueFactory(new PropertyValueFactory<User, String>("userAccount"));
        userNameCol.setCellValueFactory(new PropertyValueFactory<User, String>("userName"));
        tableView.sort();
        tableView.setItems(userList);
    }

}
