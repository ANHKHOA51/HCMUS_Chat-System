package chatapp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import chatapp.db.DBConnection;

public class User {
    private UUID id;
    private String username;
    private String displayName;
    private String email;
    private String address;
    private String password;
    private boolean gender;
    private boolean admin;
    private boolean online;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(
            UUID id, String username, String displayName, String email, String address, String password,
            boolean gender, boolean admin, boolean online,
            LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.address = address;
        this.password = password;
        this.gender = gender;
        this.admin = admin;
        this.online = online;
        this.birthday = birthday;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getBirthday() {
        if (birthday == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formatted = birthday.format(formatter);
        return formatted;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public String getCreatedAt() {
        if (createdAt == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatted = createdAt.format(formatter);
        return formatted;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        if (updatedAt == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatted = updatedAt.format(formatter);
        return formatted;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", admin=" + admin +
                ", online=" + online +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }

    // Querry
    public static List<User> getAllUser() {
        List<User> list = new ArrayList<User>();
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT * FROM users";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                User user = new User();
                user.setId(UUID.fromString(rs.getString("id")));
                user.setUsername(rs.getString("username"));
                user.setDisplayName(rs.getString("display_name"));
                user.setEmail(rs.getString("email"));
                user.setAddress(rs.getString("address"));
                user.setPassword(rs.getString("password"));
                user.setGender(rs.getBoolean("gender"));
                user.setAdmin(rs.getBoolean("admin"));
                user.setAdmin(rs.getBoolean("is_online"));
                user.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
                user.setCreatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                user.setUpdatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);

                list.add(user);
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
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
                        u.setId(UUID.fromString(rs.getString("id")));
                        u.setDisplayName(rs.getString("display_name"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setAdmin(rs.getBoolean("admin"));
                        u.setAddress(rs.getString("address"));
                        u.setGender(rs.getBoolean("gender"));
                        u.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
                        u.setCreatedAt(
                                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime()
                                        : null);
                        u.setUpdatedAt(
                                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime()
                                        : null);

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

    public static boolean register(String username, String password, String name,
            String email) {
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

            String sql = "INSERT INTO users (id, username, password, display_name, email,admin, is_online, address, birthday, gender, created_at) VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
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
}