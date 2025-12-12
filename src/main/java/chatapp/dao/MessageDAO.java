package chatapp.dao;

import chatapp.models.Message;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MessageDAO {

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
