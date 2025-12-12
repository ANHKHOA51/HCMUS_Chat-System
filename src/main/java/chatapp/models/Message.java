package chatapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.sql.Connection;

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
    // DB Methods

    public static List<Message> getMessages(UUID conversationId) {
        List<Message> list = new ArrayList<>();
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "SELECT * FROM messages WHERE conversation_id = ? ORDER BY created_at ASC";

        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            java.sql.ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Message m = new Message();
                m.setId(UUID.fromString(rs.getString("id")));
                m.setConversationId(UUID.fromString(rs.getString("conversation_id")));
                m.setSenderId(UUID.fromString(rs.getString("sender_id")));
                m.setContent(rs.getString("content"));
                m.setDeleted(rs.getBoolean("is_deleted"));
                m.setCreatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                // Other fields if needed
                list.add(m);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Message send(UUID conversationId, UUID senderId, String content) {
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "INSERT INTO messages (id, conversation_id, sender_id, content) VALUES (?, ?, ?, ?)";
        UUID msgId = UUID.randomUUID();

        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, msgId);
            ps.setObject(2, conversationId);
            ps.setObject(3, senderId);
            ps.setString(4, content);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                Message m = new Message();
                m.setId(msgId);
                m.setConversationId(conversationId);
                m.setSenderId(senderId);
                m.setContent(content);
                m.setCreatedAt(LocalDateTime.now());
                return m;
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null; // Failed
    }
}
