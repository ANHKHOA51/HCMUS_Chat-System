package chatapp.views;

import chatapp.models.User;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ProfileView extends Tab {
    public ProfileView() {
        VBox vbox = new VBox();
        GridPane gp = new GridPane();

        gp.setAlignment(Pos.TOP_CENTER);
        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(8);
        VBox.setVgrow(gp, Priority.ALWAYS);

        Label lb_user_name = new Label("User name");
        TextField tf_user_name = new TextField();
        tf_user_name.setMaxWidth(300);

        Label lb_full_name = new Label("Full name");
        TextField tf_full_name = new TextField();
        tf_full_name.setMaxWidth(300);
        tf_full_name.setDisable(true);

        Label lb_email = new Label("Email");
        TextField tf_email = new TextField();
        tf_email.setMaxWidth(300);
        tf_email.setDisable(true);

        ColumnConstraints col0 = new ColumnConstraints();
        // col0.setHalignment(HPos.RIGHT);
        col0.setPrefWidth(150);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(200); 
        col1.setHalignment(HPos.CENTER);

        gp.getColumnConstraints().addAll(col0, col1);

        gp.add(lb_full_name, 0, 12);
        gp.add(tf_full_name, 1, 12);
        gp.add(lb_user_name, 0, 13);
        gp.add(tf_user_name, 1, 13);

        gp.add(tf_email, 1, 14);
        gp.add(lb_email, 0, 14);

        Separator sep = new Separator();

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding(new Insets(10));

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.OK_DONE);
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        buttonBar.getButtons().addAll(saveButton, cancelButton);

        vbox.getChildren().addAll(gp, sep, buttonBar);

        setText("Profile");
        setContent(vbox);
    }

    public ProfileView(User u) {
        VBox vbox = new VBox();
        GridPane gp = new GridPane();

        gp.setAlignment(Pos.TOP_CENTER);
        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(8);
        VBox.setVgrow(gp, Priority.ALWAYS);

        Label lb_user_name = new Label("User name");
        TextField tf_user_name = new TextField();
        tf_user_name.setText(u.getUser_name());
        tf_user_name.setMaxWidth(300);

        Label lb_full_name = new Label("Full name");
        TextField tf_full_name = new TextField();
        tf_full_name.setMaxWidth(300);
        tf_full_name.setText(u.getName());
        tf_full_name.setDisable(true);

        Label lb_email = new Label("Email");
        TextField tf_email = new TextField();
        tf_email.setMaxWidth(300);
        tf_email.setText(u.getEmail());
        tf_email.setDisable(true);

        ColumnConstraints col0 = new ColumnConstraints();
        // col0.setHalignment(HPos.RIGHT);
        col0.setPrefWidth(150);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(200); 
        col1.setHalignment(HPos.CENTER);

        gp.getColumnConstraints().addAll(col0, col1);

        gp.add(lb_full_name, 0, 12);
        gp.add(tf_full_name, 1, 12);
        gp.add(lb_user_name, 0, 13);
        gp.add(tf_user_name, 1, 13);

        gp.add(tf_email, 1, 14);
        gp.add(lb_email, 0, 14);

        Separator sep = new Separator();

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding(new Insets(10));

        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");

        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.OK_DONE);
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        buttonBar.getButtons().addAll(saveButton, cancelButton);

        vbox.getChildren().addAll(gp, sep, buttonBar);

        setText("Profile");
        setContent(vbox);
    }
}
