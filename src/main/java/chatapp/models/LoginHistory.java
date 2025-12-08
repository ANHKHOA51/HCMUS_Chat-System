package chatapp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import chatapp.db.DBConnection;
import chatapp.dto.LoginHistoryDTO;

public class LoginHistory {
    private UUID id;
    private UUID userId;
    private LocalDateTime time;
    private LocalDateTime createdAt;

    public LoginHistory() {
    }

    public LoginHistory(UUID id, UUID userId, LocalDateTime time, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.time = time;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTime() {
        if (time == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatted = time.format(formatter);
        return formatted;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
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

    @Override
    public String toString() {
        return "LoginHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", time=" + time +
                ", createdAt=" + createdAt +
                '}';
    }

    // Query
    public static boolean saveLoginHistory(UUID id) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                INSERT INTO login_history(id, user_id, time, created_at)
                VALUES (?, ?, ?, ?)
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setObject(1, UUID.randomUUID());
            ps.setObject(2, id);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<LoginHistoryDTO> getAllLoginHistory() {
        List<LoginHistoryDTO> list = new ArrayList<LoginHistoryDTO>();

        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT ls.id AS id, u.id AS user_id, u.username AS username, u.display_name AS display_name, ls.time AS time
                FROM login_history AS ls
                JOIN users AS u ON u.id = ls.user_id
                    """;
        try {
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                LoginHistoryDTO item = new LoginHistoryDTO();
                item.setId(UUID.fromString(rs.getString("id")));
                item.setUserId(UUID.fromString(rs.getString("user_id")));
                item.setUsername(rs.getString("username"));
                item.setDisplayName(rs.getString("display_name"));
                item.setTime(rs.getTimestamp("time") != null ? rs.getTimestamp("time").toLocalDateTime() : null);
                item.setActivity("Log in");
                list.add(item);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
}
