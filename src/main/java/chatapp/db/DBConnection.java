package chatapp.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;
    private static Dotenv internalConfig = null;
    private static Dotenv externalConfig = null;

    static {
        loadConfig();
    }

    private static void loadConfig() {
        try {
            internalConfig = Dotenv.configure().directory("/").ignoreIfMissing().load();
            System.out.println("Loaded embedded configuration.");
        } catch (Exception e) {
            System.err.println("Warning: Could not load embedded .env");
        }

        try {
            externalConfig = Dotenv.configure().ignoreIfMissing().load();
            if (externalConfig.entries().isEmpty()) {
                externalConfig = null;
            } else {
                System.out.println("Loaded external configuration.");
            }
        } catch (Exception e) {
            externalConfig = null;
        }
    }

    public static String get(String key) {
        String value = null;

        if (externalConfig != null) {
            value = externalConfig.get(key);
        }

        if (value == null && internalConfig != null) {
            value = internalConfig.get(key);
        }

        return value;
    }

    public static String get(String key, String defaultValue) {
        String val = get(key);
        return (val == null) ? defaultValue : val;
    }

    public static Connection getConnection() {
        if (connection == null) {
            try {
                String url = get("SUPABASE_JDBC_URL");
                String user = get("SUPABASE_DB_USER");
                String password = get("SUPABASE_DB_PASS");

                if (url == null || user == null || password == null) {
                    System.err.println("Database configuration is missing!");
                    return null;
                }

                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
