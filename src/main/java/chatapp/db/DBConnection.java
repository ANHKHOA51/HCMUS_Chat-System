package chatapp.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Dotenv dotenv;
                try {
                    // Try loading from filesystem first (Dev environment)
                    dotenv = Dotenv.load();
                } catch (Exception e) {
                    // Fallback to loading from classpath (Prod/Jar environment)
                    System.out.println("Could not load .env from filesystem, trying classpath...");
                    dotenv = Dotenv.configure().directory("/").load();
                }

                String url = dotenv.get("SUPABASE_JDBC_URL");
                String user = dotenv.get("SUPABASE_DB_USER");
                String password = dotenv.get("SUPABASE_DB_PASS");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }
}
