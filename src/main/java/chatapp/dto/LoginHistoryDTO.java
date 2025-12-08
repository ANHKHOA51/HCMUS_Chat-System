package chatapp.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class LoginHistoryDTO {
    private UUID id;
    private UUID userId;
    private String username;
    private String displayName;
    private LocalDateTime time;
    private String activity;

    public LoginHistoryDTO() {
    }

    public LoginHistoryDTO(UUID id, UUID userId, String username, String displayName, LocalDateTime time,
            String activity) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.displayName = displayName;
        this.time = time;
        this.activity = activity;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    @Override
    public String toString() {
        return "LoginHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", time=" + time +
                ", activity='" + activity + '\'' +
                '}';
    }
}
