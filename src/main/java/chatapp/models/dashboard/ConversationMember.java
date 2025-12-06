package chatapp.models.dashboard;

import java.time.LocalDateTime;
import java.util.UUID;

public class ConversationMember {
    private UUID conversationId;
    private UUID userId;
    private UUID lastReadMessageId;
    private String role;
    private LocalDateTime joinedAt;
    private LocalDateTime lastReadAt;

    public ConversationMember() {
    }

    public ConversationMember(UUID conversationId, UUID userId,
            UUID lastReadMessageId, String role,
            LocalDateTime joinedAt, LocalDateTime lastReadAt) {
        this.conversationId = conversationId;
        this.userId = userId;
        this.lastReadMessageId = lastReadMessageId;
        this.role = role;
        this.joinedAt = joinedAt;
        this.lastReadAt = lastReadAt;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getLastReadMessageId() {
        return lastReadMessageId;
    }

    public void setLastReadMessageId(UUID lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    public LocalDateTime getLastReadAt() {
        return lastReadAt;
    }

    public void setLastReadAt(LocalDateTime lastReadAt) {
        this.lastReadAt = lastReadAt;
    }

    @Override
    public String toString() {
        return "ConversationMember{" +
                "conversationId=" + conversationId +
                ", userId=" + userId +
                ", lastReadMessageId=" + lastReadMessageId +
                ", role='" + role + '\'' +
                ", joinedAt=" + joinedAt +
                ", lastReadAt=" + lastReadAt +
                '}';
    }
}
