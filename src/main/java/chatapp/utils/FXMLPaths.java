package chatapp.utils;

public class FXMLPaths {

    public static class Dashboard {
        private static final String DASHBOARD_BASE = "/chatapp/views/dashboard/";

        public static final String USER = DASHBOARD_BASE + "user.fxml";
        public static final String CHAT_GROUP = DASHBOARD_BASE + "chatGroup.fxml";
        public static final String LOGIN_HISTORY = DASHBOARD_BASE + "loginHistory.fxml";
        public static final String REGISTRATION = DASHBOARD_BASE + "registration.fxml";
        public static final String REPORT = DASHBOARD_BASE + "report.fxml";
        public static final String ACTIVITY = DASHBOARD_BASE + "activity.fxml";
        public static final String FRIEND = DASHBOARD_BASE + "friend.fxml";
    }

    public static class Auth {
        private static final String AUTH_BASE = "/chatapp/views/auth";

        public static final String LOG_IN = AUTH_BASE + "loginForm.fxml";
        public static final String REGISTER = AUTH_BASE + "registerForm.fxml";
    }

    private FXMLPaths() {
    }
}
