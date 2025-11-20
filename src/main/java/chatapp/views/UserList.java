package chatapp.views;

import chatapp.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.collections.ObservableList;

public class UserList extends BorderPane {
    TextField filterField;
    ListView<User> userListView;
    
    public UserList() {
        filterField = new TextField();
        filterField.setMaxWidth(200);

        HBox topBox = new HBox(filterField);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(10)); // optional spacing
        this.setTop(topBox);
        this.setCenter(userListView);
        setDefaultCellFactory();
    }

    public UserList(ObservableList<User> users) {
        userListView = new ListView<>(users);
        filterField = new TextField();
        filterField.setMaxWidth(200);

        HBox topBox = new HBox(filterField);
        topBox.setAlignment(Pos.CENTER_RIGHT);
        topBox.setPadding(new Insets(10)); // optional spacing
        this.setTop(topBox);
        this.setCenter(userListView);
        setDefaultCellFactory();
    }

    private void setDefaultCellFactory() {
        userListView.setCellFactory(list -> new UserListCell());
    }
}
