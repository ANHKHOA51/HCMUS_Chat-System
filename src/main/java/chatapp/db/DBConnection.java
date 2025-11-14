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
                Dotenv dotenv = Dotenv.load();
                String url = dotenv.get("SUPABASE_JDBC_URL");
                System.out.println(url);
                String user = dotenv.get("SUPABASE_DB_USER");
                System.out.println(user);
                String password = dotenv.get("SUPABASE_DB_PASS");
                System.out.println(password);
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected!");
            } catch (SQLException e) {
                
            }
        }
        return connection;
    }
}
