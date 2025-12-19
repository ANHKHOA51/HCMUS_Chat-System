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
        return send(UUID.randomUUID(), conversationId, senderId, content);
    }

    public static Message send(UUID msgId, UUID conversationId, UUID senderId, String content) {
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "INSERT INTO messages (id, conversation_id, sender_id, content) VALUES (?, ?, ?, ?)";

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

    public static boolean deleteMessage(UUID messageId) {
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "UPDATE messages SET is_deleted = TRUE WHERE id = ?";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, messageId);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteAllMessages(UUID conversationId) {
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "UPDATE messages SET is_deleted = TRUE WHERE conversation_id = ?";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Message> searchMessages(UUID conversationId, String query) {
        List<Message> list = new ArrayList<>();
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "SELECT * FROM messages WHERE conversation_id = ? AND content ILIKE ? AND is_deleted = FALSE ORDER BY created_at ASC";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ps.setString(2, "%" + query + "%");
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
                list.add(m);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Message> searchAllMessages(UUID userId, String query) {
        List<Message> list = new ArrayList<>();
        Connection conn = chatapp.db.DBConnection.getConnection();
        // Join with conversation_members to ensure user is part of conversation
        String sql = """
                    SELECT m.*
                    FROM messages m
                    JOIN conversation_members cm ON m.conversation_id = cm.conversation_id
                    WHERE cm.user_id = ?
                    AND m.content ILIKE ?
                    AND m.is_deleted = FALSE
                    ORDER BY m.created_at DESC
                """;
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setString(2, "%" + query + "%");
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
                list.add(m);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Message> getAllReadableMessages(UUID userId) {
        List<Message> list = new ArrayList<>();
        Connection conn = chatapp.db.DBConnection.getConnection();
        // Join with conversation_members to ensure user is part of conversation
        String sql = """
                    SELECT m.*
                    FROM messages m
                    JOIN conversation_members cm ON m.conversation_id = cm.conversation_id
                    WHERE cm.user_id = ?
                    AND m.is_deleted = FALSE
                    ORDER BY m.created_at DESC
                """;
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
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
                list.add(m);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean deleteMessagesFromUser(UUID conversationId, UUID userId) {
        Connection conn = chatapp.db.DBConnection.getConnection();
        String sql = "UPDATE messages SET is_deleted = TRUE WHERE conversation_id = ? AND sender_id = ?";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
