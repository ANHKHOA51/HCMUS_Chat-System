package chatapp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private String updateAt;
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public boolean isOnline() {
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

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public User() {

    }

    public User(String id, String userAccount, String userName, String address, String birthday, boolean gender,
            String email, String status) {
        this.id = id;
        this.name = userName;
        this.user_name = userAccount;
        this.address = address;
        this.birthday = birthday;
        this.gender = gender;
        this.email = email;
        this.status = status;

        this.createAt = "" + LocalDateTime.now();
        this.updateAt = "" + LocalDateTime.now();
    }

    public static User login(String user_name) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user_name);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        User u = new User();
                        u.setId(rs.getString("id"));
                        u.setName(rs.getString("display_name"));
                        u.setUser_name(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setAdmin(rs.getBoolean("admin"));
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

    @Override
    public String toString() {
        return this.user_name;
    }
}
