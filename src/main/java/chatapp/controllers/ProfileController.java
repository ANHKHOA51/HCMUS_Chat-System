package chatapp.controllers;

import chatapp.models.User;
import chatapp.views.ProfileView;
import javafx.scene.control.Tab;

public class ProfileController {
    private ProfileView profile;

    public ProfileController() {
        profile = new ProfileView();
    }

    public ProfileController(User u) {
        profile = new ProfileView(u);
    }

    public Tab getProfileView() {
        this.profile.setClosable(false);
        return this.profile;
    }
}
