package chatapp.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import chatapp.db.DBConnection;
import chatapp.dto.ReportDTO;

public class Report {
    private UUID id;
    private UUID reportedBy;
    private UUID reportedUserId;
    private String content;
    private LocalDateTime createdAt;

    public Report() {
    }

    public Report(UUID id, UUID reportedBy, UUID reportedUserId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.reportedBy = reportedBy;
        this.reportedUserId = reportedUserId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(UUID reportedBy) {
        this.reportedBy = reportedBy;
    }

    public UUID getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(UUID reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public static List<ReportDTO> getListReports() {
        List<ReportDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT r.id AS id, rpr.id AS reporter_id, rpr.username AS reporter_username, r.content AS content,
                    rpt.id AS reported_id, rpt.username AS reported_username, r.created_at AS time
                FROM reports AS r
                JOIN users AS rpr ON rpr.id = r.reported_by
                JOIN users AS rpt ON rpt.id = r.reported_user_id
                    """;

        try {
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                ReportDTO item = new ReportDTO();
                item.setId(UUID.fromString(rs.getString("id")));
                item.setReporterId(UUID.fromString(rs.getString("reporter_id")));
                item.setReporterUsername(rs.getString("reporter_username"));
                item.setContent(rs.getString("content"));
                item.setReportedUserId(UUID.fromString(rs.getString("reported_id")));
                item.setReportedUserName(rs.getString("reported_username"));
                item.setTime(
                        rs.getTimestamp("time") != null ? rs.getTimestamp("time").toLocalDateTime() : null);

                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean deleteReport(UUID id) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                DELETE FROM reports WHERE id = ?
                    """;

        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setObject(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addReport(UUID reportedBy, UUID reportedUser, String content) {
        Connection conn = DBConnection.getConnection();
        String sql = "INSERT INTO reports (id, reported_by, reported_user_id, content, created_at) VALUES (?, ?, ?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, UUID.randomUUID());
            ps.setObject(2, reportedBy);
            ps.setObject(3, reportedUser);
            ps.setString(4, content);
            ps.setObject(5, LocalDateTime.now());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
