package chatapp.dao;

import chatapp.db.DBConnection;
import chatapp.models.User;
import chatapp.dto.LoginHistoryDTO;

import java.sql.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDAO {

    public static User login(String user_name, String password) {
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, user_name);
                ps.setString(2, password);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToUser(rs);
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

    public static boolean register(String username, String password, String name, String email, String address,
            String birthday, boolean gender) {
        try {
            Connection conn = DBConnection.getConnection();
            String checkSql = "SELECT id FROM users WHERE username = ?";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, username);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        return false; 
                    }
                }
            }

            String sql = "INSERT INTO users (id, username, password, display_name, email,admin, is_online, address, birthday, gender, created_at) VALUES (?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setObject(1, java.util.UUID.randomUUID());
                ps.setString(2, username);
                ps.setString(3, password);
                ps.setString(4, name);
                ps.setString(5, email);
                ps.setBoolean(6, false);
                ps.setBoolean(7, false);
                ps.setString(8, address);

                if (birthday == null || birthday.trim().isEmpty()) {
                    ps.setNull(9, java.sql.Types.DATE);
                } else {
                    ps.setDate(9, java.sql.Date.valueOf(birthday));
                }

                ps.setBoolean(10, gender);
                ps.setTimestamp(11, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));

                int rowsAffected = ps.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isEmailExists(String email) {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT id FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updatePassword(String email, String newPassword) {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE users SET password = ? WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateUser(User user) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                UPDATE users
                SET display_name = ?, email = ?, address = ?, birthday = ?, gender = ?, updated_at = ?
                WHERE id = ?
                """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getDisplayName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getAddress());

            if (user.getBirthdayUnformat() == null) {
                ps.setNull(4, java.sql.Types.DATE);
            } else {
                ps.setDate(4, java.sql.Date.valueOf(user.getBirthdayUnformat()));
            }

            ps.setBoolean(5, user.isGender());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setObject(7, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean changePassword(UUID userId, String newPassword) {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static User getUser(String field, Object value) {
        List<String> listField = List.of("id", "username", "email");
        if (!listField.contains(field)) {
            throw new IllegalArgumentException("Invalid field name!");
        }

        User user = null;
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM users WHERE " + field + " = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, value);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                user = mapResultSetToUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    public static boolean updateFieldUser(String field, Object value, String filterField, Object filterValue) {
        List<String> listField = List.of("username", "email", "address", "display_name", "admin", "password",
                "birthday", "gender", "is_online", "lock");
        if (!listField.contains(field)) {
            throw new IllegalArgumentException("Invalid field name!");
        }

        List<String> listFilterField = List.of("id", "username", "email");
        if (!listFilterField.contains(filterField)) {
            throw new IllegalArgumentException("Invalid filter field name!");
        }

        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE users SET " + field + " = ? WHERE " + filterField + " = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, value);
            ps.setObject(2, filterValue);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getAllUser() {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();

        String sql = "SELECT * FROM users ORDER BY id ASC";
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<LoginHistoryDTO> getLoginHistory(UUID userId) {
        List<LoginHistoryDTO> list = new ArrayList<>();

        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT ls.id AS id, u.id AS user_id, u.username AS username, ls.time AS time
                FROM login_history AS ls
                JOIN users AS u ON u.id = ls.user_id
                WHERE ls.user_id = ?
                """;

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                LoginHistoryDTO loginHistory = new LoginHistoryDTO();
                loginHistory.setId(UUID.fromString(rs.getString("id")));
                loginHistory.setUserId(UUID.fromString(rs.getString("user_id")));
                loginHistory.setUsername(rs.getString("username"));
                loginHistory
                        .setTime(rs.getTimestamp("time") != null ? rs.getTimestamp("time").toLocalDateTime() : null);
                loginHistory.setActivity("Log in");

                list.add(loginHistory);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setDisplayName(rs.getString("display_name"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setPassword(rs.getString("password"));
        user.setGender(rs.getBoolean("gender"));
        user.setAdmin(rs.getBoolean("admin"));
        user.setOnline(rs.getBoolean("is_online"));
        user.setLock(rs.getBoolean("lock"));
        user.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
        user.setCreatedAt(
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        user.setUpdatedAt(
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return user;
    }
}
