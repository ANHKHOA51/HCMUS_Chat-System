package chatapp;

import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;

public class MessageList extends Tab {
    MessageList(ObservableList<String> msgs) {
        ListView<String> view = new ListView<>(msgs);

        SplitPane layout = new SplitPane();

        layout.setDividerPositions(0.3);
        layout.getItems().addAll(view);

        view.setFixedCellSize(50);
        HBox container = new HBox();
        container.getChildren().addAll(layout);

        setText("Messages");
        setClosable(false);
        setContent(container);
    }
}
