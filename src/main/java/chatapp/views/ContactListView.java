package chatapp.views;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

public class ContactListView extends ListView<String> {
    public ContactListView() {
        super();
        setFixedCellSize(50);
        // center text and enlarge font
        setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                }
            };
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-font-size: 16px;"); // increase text size
            return cell;
        });
    }

    public ContactListView(ObservableList<String> contact) {
        super();
        setItems(contact);
        setFixedCellSize(50);
        setCellFactory(lv -> {
            ListCell<String> cell = new ListCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    setText(empty || item == null ? null : item);
                }
            };
            cell.setAlignment(Pos.CENTER);
            cell.setStyle("-fx-font-size: 16px;");
            return cell;
        });
    }

    public ListView<String> getContactList() {
        return this;
    }
}
