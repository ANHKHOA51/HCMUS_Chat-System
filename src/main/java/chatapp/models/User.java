package chatapp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import chatapp.db.DBConnection;

import java.time.LocalDateTime;

public class User {
    private String id;
    private String name;
    private String user_name;
    private String email;
    private boolean isAdmin;
    private boolean isOnline;
    private String address;
    private String birthday;
    private boolean gender;
    private String createAt;
    // private String updateAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean getIsAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public User() {

    }

    public User(String id, String user_name, String name, String address, String birthday, boolean gender,
            String email, boolean isOnline, boolean isAdmin) {
        this.id = id;
        this.name = name;
        this.user_name = user_name;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.isOnline = isOnline;
        this.isAdmin = isAdmin;

        this.createAt = "" + LocalDateTime.now();
    }

    public static User login(String user_name, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user_name);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.setId(rs.getString("id"));
                        u.setName(rs.getString("display_name"));
                        u.setUser_name(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setAdmin(rs.getBoolean("admin"));
                        u.setAddress(rs.getString("address"));
                        u.setBirthday(rs.getString("birthday"));
                        u.setGender(rs.getBoolean("gender"));
                        u.setCreateAt(rs.getString("created_at"));
                        // u.setUpdateAt(rs.getString("updated_at"));
                        return u;
                    } else {
                        return null;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean register(String username, String password, String name, String email) {
        try {
            Connection conn = DBConnection.getConnection();
            // Check if username already exists
            String checkSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, username);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        return false; // Username already exists
                    }
                }
            }

            String sql = "INSERT INTO users (id, username, password, display_name, email, admin, is_online, address, birthday, gender, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String now = LocalDateTime.now().toString();
                ps.setString(1, java.util.UUID.randomUUID().toString());
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, name);
                ps.setString(5, email);
                ps.setBoolean(6, false); // Default not admin
                ps.setBoolean(7, false); // Default offline
                ps.setString(8, ""); // Default address
                ps.setString(9, ""); // Default birthday
                ps.setBoolean(10, true); // Default gender (true for male, just a default)
                ps.setString(11, now);


                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String toString() {
        return this.user_name;
    }
}
