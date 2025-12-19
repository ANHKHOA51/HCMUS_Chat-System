package chatapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Message {
    private UUID id;
    private UUID conversationId;
    private UUID senderId;
    private UUID replyToMessageId;
    private int conversationSeq;
    private String clientMessageId;
    private String content;
    private boolean deleted;
    private LocalDateTime createdAt;

    public Message() {
    }

    public Message(UUID id, UUID conversationId, UUID senderId, UUID replyToMessageId,
            int conversationSeq, String clientMessageId, String content, boolean deleted, LocalDateTime createdAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.replyToMessageId = replyToMessageId;
        this.conversationSeq = conversationSeq;
        this.clientMessageId = clientMessageId;
        this.content = content;
        this.deleted = deleted;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getSenderId() {
        return senderId;
    }

    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    public UUID getReplyToMessageId() {
        return replyToMessageId;
    }

    public void setReplyToMessageId(UUID replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }

    public int getConversationSeq() {
        return conversationSeq;
    }

    public void setConversationSeq(int conversationSeq) {
        this.conversationSeq = conversationSeq;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
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

    public LocalDateTime getTimestamp() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", conversationId=" + conversationId +
                ", senderId=" + senderId +
                ", replyToMessageId=" + replyToMessageId +
                ", conversationSeq=" + conversationSeq +
                ", clientMessageId='" + clientMessageId + '\'' +
                ", content='" + content + '\'' +
                ", deleted=" + deleted +
                ", createdAt=" + createdAt +
                '}';
    }
    // DB Methods removed and moved to MessageDAO
}
