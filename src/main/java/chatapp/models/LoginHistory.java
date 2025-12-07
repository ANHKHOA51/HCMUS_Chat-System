package chatapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

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
}
