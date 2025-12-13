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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class ProfileView extends Tab {
    private Button signOutButton;
    private TextField tf_user_name;
    private TextField tf_password;
    private TextField tf_full_name;
    private TextField tf_email;
    private TextField tf_address;
    private DatePicker dp_birthday;
    private ToggleGroup genderGroup;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private Button saveButton;
    private Button cancelButton;

    public ProfileView(User u) {
        VBox vbox = new VBox();
        GridPane gp = new GridPane();

        gp.setAlignment(Pos.TOP_CENTER);
        gp.setPadding(new Insets(10));
        gp.setHgap(10);
        gp.setVgap(8);
        VBox.setVgrow(gp, Priority.ALWAYS);

        Label lb_user_name = new Label("Username");
        tf_user_name = new TextField();
        tf_user_name.setText(u.getUsername());
        tf_user_name.setMaxWidth(300);
        tf_user_name.setDisable(true); // Username immutable

        Label lb_password = new Label("New password");
        tf_password = new TextField();
        tf_password.setMaxWidth(300);
        tf_password.setPromptText("Leave empty to keep");

        Label lb_full_name = new Label("Display name");
        tf_full_name = new TextField();
        tf_full_name.setMaxWidth(300);
        tf_full_name.setText(u.getDisplayName());

        Label lb_email = new Label("Email");
        tf_email = new TextField();
        tf_email.setMaxWidth(300);
        tf_email.setText(u.getEmail());

        Label lb_address = new Label("Address");
        tf_address = new TextField();
        tf_address.setMaxWidth(300);
        tf_address.setText(u.getAddress());

        Label lb_birthday = new Label("Birthday");
        dp_birthday = new DatePicker();
        dp_birthday.setValue(u.getBirthdayUnformat());

        Label lb_gender = new Label("Gender");
        genderGroup = new ToggleGroup();
        rb_male = new RadioButton("Male");
        rb_female = new RadioButton("Female");
        rb_male.setToggleGroup(genderGroup);
        rb_female.setToggleGroup(genderGroup);
        if (u.isGender()) {
            rb_male.setSelected(true);
        } else {
            rb_female.setSelected(true);
        }
        HBox genderBox = new HBox(10, rb_male, rb_female);
        genderBox.setAlignment(Pos.CENTER_LEFT);

        ColumnConstraints col0 = new ColumnConstraints();
        col0.setPrefWidth(150);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPrefWidth(200);
        col1.setHalignment(HPos.CENTER);

        gp.getColumnConstraints().addAll(col0, col1);

        gp.add(lb_full_name, 0, 12);
        gp.add(tf_full_name, 1, 12);

        gp.add(lb_email, 0, 13);
        gp.add(tf_email, 1, 13);

        gp.add(lb_address, 0, 14);
        gp.add(tf_address, 1, 14);

        gp.add(lb_birthday, 0, 15);
        gp.add(dp_birthday, 1, 15);

        gp.add(lb_gender, 0, 16);
        gp.add(genderBox, 1, 16);

        gp.add(lb_password, 0, 17);
        gp.add(tf_password, 1, 17);

        gp.add(lb_user_name, 0, 18);
        gp.add(tf_user_name, 1, 18);

        Separator sep = new Separator();

        ButtonBar buttonBar = new ButtonBar();
        buttonBar.setPadding(new Insets(10));

        saveButton = new Button("Save");
        cancelButton = new Button("Cancel");
        signOutButton = new Button("Sign Out");

        ButtonBar.setButtonData(saveButton, ButtonBar.ButtonData.OK_DONE);
        ButtonBar.setButtonData(cancelButton, ButtonBar.ButtonData.CANCEL_CLOSE);

        buttonBar.getButtons().addAll(saveButton, cancelButton, signOutButton);

        vbox.getChildren().addAll(gp, sep, buttonBar);

        setText("Profile");
        setContent(vbox);
    }

    // Default constructor for testing or initial view
    public ProfileView() {
        this(new User(java.util.UUID.randomUUID(), "", "", "", "", "", false, false, false, java.time.LocalDate.now(),
                null, null));
    }

    public Button getSignOutButton() {
        return signOutButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public TextField getDisplayNameField() {
        return tf_full_name;
    }

    public TextField getEmailField() {
        return tf_email;
    }

    public TextField getPasswordField() {
        return tf_password;
    }

    public TextField getUsernameField() {
        return tf_user_name;
    }

    public TextField getAddressField() {
        return tf_address;
    }

    public DatePicker getBirthdayPicker() {
        return dp_birthday;
    }

    public ToggleGroup getGenderGroup() {
        return genderGroup;
    }
}
