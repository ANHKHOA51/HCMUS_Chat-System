package chatapp.views;

import chatapp.models.User;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ContactListView extends ListView<User> {
    public ContactListView() {
        super();
        setFixedCellSize(50);
        // center text and enlarge font
        setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<>() {
                @Override
                protected void updateItem(User u, boolean empty) {
                    if (empty || u == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    super.updateItem(u, empty);
                    String display = (u.toString() == null ? "" : u.toString());
                    setText(display);
                }
            };
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-font-size: 16px;"); // increase text size
            return cell;
        });
    }

    public ContactListView(ObservableList<User> contact) {
        super();
        setItems(contact);
        setFixedCellSize(50);
        setCellFactory(lv -> {
            ListCell<User> cell = new ListCell<>() {
                @Override
                protected void updateItem(User u, boolean empty) {
                    if (empty || u == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    super.updateItem(u, empty);
                    String display = (u.toString() == null ? "" : u.toString());
                    setText(display);
                }
            };
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-font-size: 16px;");
            return cell;
        });
    }

    public ListView<User> getContactList() {
        return this;
    }
}
