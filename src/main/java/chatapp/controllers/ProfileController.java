package chatapp.controllers;

import chatapp.models.User;
import chatapp.views.ProfileView;
import javafx.scene.control.Tab;
import javafx.scene.control.RadioButton;
import java.time.LocalDate;

public class ProfileController {
    private ProfileView profile;
    private User user;

    public ProfileController(User u) {
        this.user = u;
        profile = new ProfileView(u);
        profile.getSaveButton().setOnAction(e -> handleSave());
        profile.getCancelButton().setOnAction(e -> {
            // Revert changes
            profile.getDisplayNameField().setText(u.getDisplayName());
            profile.getEmailField().setText(u.getEmail());
            profile.getPasswordField().setText("");
            profile.getAddressField().setText(u.getAddress());
            profile.getBirthdayPicker().setValue(u.getBirthdayUnformat());

            // Revert Gender
            // This is a bit manual since we don't have direct access to radios via public
            // getter easily if we only have group
            // Ideally we iterate options or just re-select based on user object
            if (user.isGender()) {
                // We need to match the toggle corresponding to Male...
                // It's cleaner if ProfileView exposed a method "setGender(boolean)"
                // But for now, let's rely on the assumption that we can find the toggle or we
                // accept we might need to expose radios.
                // Actually ProfileView exposed the group.
                // Let's iterate toggles.
                profile.getGenderGroup().getToggles().forEach(t -> {
                    RadioButton rb = (RadioButton) t;
                    if (rb.getText().equals("Male"))
                        rb.setSelected(true);
                });
            } else {
                profile.getGenderGroup().getToggles().forEach(t -> {
                    RadioButton rb = (RadioButton) t;
                    if (rb.getText().equals("Female"))
                        rb.setSelected(true);
                });
            }
        });
    }

    public Tab getProfileView() {
        this.profile.setClosable(false);
        return this.profile;
    }

    public void setOnSignOut(Runnable handler) {
        profile.getSignOutButton().setOnAction(e -> handler.run());
    }

    private void handleSave() {
        String displayName = profile.getDisplayNameField().getText();
        // String email = profile.getEmailField().getText(); // Email editing if enabled
        String newPass = profile.getPasswordField().getText();
        String address = profile.getAddressField().getText();
        LocalDate birthday = profile.getBirthdayPicker().getValue();
        boolean gender = true; // Default

        if (profile.getGenderGroup().getSelectedToggle() != null) {
            RadioButton selected = (RadioButton) profile.getGenderGroup().getSelectedToggle();
            gender = selected.getText().equals("Male");
        }

        // Update user object locally
        user.setDisplayName(displayName);
        user.setAddress(address);
        user.setBirthday(birthday);
        user.setGender(gender);

        boolean updateInfo = chatapp.dao.UserDAO.updateUser(user);
        boolean updatePass = true;

        if (newPass != null && !newPass.isEmpty()) {
            updatePass = chatapp.dao.UserDAO.changePassword(user.getId(), newPass);
        }

        if (updateInfo && updatePass) {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            alert.setTitle("Profile Saved");
            alert.setContentText("Your profile has been updated.");
            alert.showAndWait();
            // Clear password field after save
            profile.getPasswordField().setText("");
        } else {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to update profile.");
            alert.showAndWait();
        }
    }
}
