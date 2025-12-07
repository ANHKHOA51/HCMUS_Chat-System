package chatapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class FriendShip {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private UUID requesterId;
    private String status;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FriendShip() {
    }

    public FriendShip(UUID id, UUID userId, UUID friendId, UUID requesterId,
            String status, LocalDateTime acceptedAt, LocalDateTime createdAt,
            LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.friendId = friendId;
        this.requesterId = requesterId;
        this.status = status;
        this.acceptedAt = acceptedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public UUID getFriendId() {
        return friendId;
    }

    public void setFriendId(UUID friendId) {
        this.friendId = friendId;
    }

    public UUID getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(UUID requesterId) {
        this.requesterId = requesterId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAcceptedAt() {
        if (acceptedAt == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
        String formatted = acceptedAt.format(formatter);
        return formatted;
    }

    public void setAcceptedAt(LocalDateTime acceptedAt) {
        this.acceptedAt = acceptedAt;
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
        return "FriendShip{" +
                "id=" + id +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", requesterId=" + requesterId +
                ", status='" + status + '\'' +
                ", acceptedAt=" + acceptedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
