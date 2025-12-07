package chatapp.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ReportDTO {
    private UUID id;
    private UUID reporterId;
    private String reporterUsername;
    private String content;
    private UUID reportedUserId;
    private String reportedUserName;
    private LocalDateTime time;

    public ReportDTO() {
    }

    public ReportDTO(UUID id, UUID reporterId, String reporterUsername, String content, UUID reportedUserId,
            String reportedUserName, LocalDateTime time) {
        this.id = id;
        this.reporterId = reporterId;
        this.reporterUsername = reporterUsername;
        this.content = content;
        this.reportedUserId = reportedUserId;
        this.reportedUserName = reportedUserName;
        this.time = time;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getReporterId() {
        return reporterId;
    }

    public void setReporterId(UUID reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterUsername() {
        return reporterUsername;
    }

    public void setReporterUsername(String reporterUsername) {
        this.reporterUsername = reporterUsername;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UUID getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(UUID reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public void setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
    }

    public String getTime() {
        if (time == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatted = time.format(formatter);
        return formatted;
    }

    public LocalDateTime getTimeUnformat() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}
