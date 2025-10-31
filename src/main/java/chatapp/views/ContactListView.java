package chatapp.views;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class ContactListView extends ListView<String> {
    public ContactListView() {
        super();
        setFixedCellSize(50);
    }

    public ContactListView(ObservableList<String> contact) {
        super();
        setItems(contact);
        setFixedCellSize(50);
    }

    public ListView<String> getContactList() {
        return this;
    }
}
