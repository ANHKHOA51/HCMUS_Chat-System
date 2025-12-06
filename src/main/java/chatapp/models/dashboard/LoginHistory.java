package chatapp.models.dashboard;

import java.time.LocalDateTime;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
