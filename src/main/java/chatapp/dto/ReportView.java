package chatapp.dto;

public class ReportView {
    private int id;
    private int reporterId;
    private String reporterName;
    private String content;
    private int reportedUserId;
    private String reportedUserName;

    public ReportView(int id, int reporterId, String reporterName, String content, int reportedUserId,
            String reportedUserName) {
        this.id = id;
        this.reporterId = reporterId;
        this.reporterName = reporterName;
        this.content = content;
        this.reportedUserId = reportedUserId;
        this.reportedUserName = reportedUserName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getReporterId() {
        return reporterId;
    }

    public void setReporterId(int reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getReportedUserId() {
        return reportedUserId;
    }

    public void setReportedUserId(int reportedUserId) {
        this.reportedUserId = reportedUserId;
    }

    public String getReportedUserName() {
        return reportedUserName;
    }

    public void setReportedUserName(String reportedUserName) {
        this.reportedUserName = reportedUserName;
    }

}
