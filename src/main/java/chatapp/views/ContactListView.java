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
        setCellFactory(lv -> createCell());
    }

    public ContactListView(ObservableList<User> contact) {
        super();
        setItems(contact);
        setFixedCellSize(50);
        setCellFactory(lv -> createCell());
    }

    private java.util.function.Consumer<User> onReportSpam;

    public void setOnReportSpam(java.util.function.Consumer<User> onReportSpam) {
        this.onReportSpam = onReportSpam;
        refresh(); 
    }

    public ListView<User> getContactList() {
        return this;
    }

    private javafx.scene.control.ListCell<User> createCell() {
        javafx.scene.control.ListCell<User> cell = new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(User u, boolean empty) {
                super.updateItem(u, empty);
                if (empty || u == null) {
                    setText(null);
                    setGraphic(null);
                    setContextMenu(null);
                } else {
                    String display = (u.getDisplayName() != null && !u.getDisplayName().isEmpty()) ? u.getDisplayName()
                            : u.getUsername();
                    setText(display);

                    if (!(u instanceof chatapp.models.GroupUser)) {
                        javafx.scene.control.ContextMenu cm = new javafx.scene.control.ContextMenu();
                        javafx.scene.control.MenuItem reportItem = new javafx.scene.control.MenuItem("Report Spam");
                        reportItem.setOnAction(e -> {
                            if (onReportSpam != null)
                                onReportSpam.accept(u);
                        });
                        cm.getItems().add(reportItem);
                        setContextMenu(cm);
                    } else {
                        setContextMenu(null);
                    }
                }
            }
        };
        cell.setAlignment(Pos.CENTER);
        cell.setStyle("-fx-font-size: 16px;");
        return cell;
    }
}
