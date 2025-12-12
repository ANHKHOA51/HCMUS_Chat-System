package chatapp.dao;

import chatapp.db.DBConnection;
import chatapp.dto.ChatGroupDTO;
import chatapp.models.Conversation;
import chatapp.models.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ConversationDAO {

    public static List<ChatGroupDTO> getListChatGroup() {
        List<ChatGroupDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT c.id AS id, c.title AS group_name, c.created_at AS created_at, u.username AS creator, COUNT(cm.user_id) AS num_members
                FROM conversations AS c
                JOIN conversation_members AS cm ON cm.conversation_id = c.id
                JOIN users AS u ON u.id = c.created_by
                WHERE c.isgroup = TRUE
                GROUP BY c.id, c.title, c.created_at, u.username
                    """;
        try {
            Statement st = conn.createStatement();

            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                ChatGroupDTO item = new ChatGroupDTO();
                item.setId(UUID.fromString(rs.getString("id")));
                item.setGroupName(rs.getString("group_name"));
                item.setCreatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                item.setCreator(rs.getString("creator"));
                item.setNumMember(rs.getInt("num_members"));

                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<User> getListMembers(UUID conversation_id, String role) {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT u.username AS username, u.gender AS gender, u.email AS email
                FROM conversation_members AS cm
                JOIN users AS u ON u.id = cm.user_id
                WHERE cm.conversation_id = ? AND cm.role = ?
                    """;

        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setObject(1, conversation_id);
            ps.setString(2, role);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setUsername(rs.getString("username"));
                user.setGender(rs.getBoolean("gender"));
                user.setEmail(rs.getString("email"));

                list.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Conversation getPrivateConversation(UUID user1, UUID user2) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                    SELECT c.*
                    FROM conversations c
                    JOIN conversation_members cm1 ON c.id = cm1.conversation_id
                    JOIN conversation_members cm2 ON c.id = cm2.conversation_id
                    WHERE c.isGroup = FALSE
                    AND cm1.user_id = ?
                    AND cm2.user_id = ?
                    LIMIT 1;
                """;

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, user1);
            ps.setObject(2, user2);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Conversation c = new Conversation();
                c.setId(UUID.fromString(rs.getString("id")));
                c.setGroup(rs.getBoolean("isGroup"));
                c.setTitle(rs.getString("title"));
                c.setCreatedBy(rs.getString("created_by") != null ? UUID.fromString(rs.getString("created_by")) : null);
                c.setCreatedAt(
                        rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Conversation createPrivateConversation(UUID user1, UUID user2) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Create Conversation
            UUID convId = UUID.randomUUID();
            String sqlConv = "INSERT INTO conversations (id, isGroup, created_by) VALUES (?, ?, ?)";
            PreparedStatement psConv = conn.prepareStatement(sqlConv);
            psConv.setObject(1, convId);
            psConv.setBoolean(2, false);
            psConv.setObject(3, user1); // Initiator
            psConv.executeUpdate();

            // 2. Add Members
            String sqlMem = "INSERT INTO conversation_members (conversation_id, user_id, role) VALUES (?, ?, ?)";
            PreparedStatement psMem = conn.prepareStatement(sqlMem);

            // Member 1
            psMem.setObject(1, convId);
            psMem.setObject(2, user1);
            psMem.setString(3, "member");
            psMem.executeUpdate();

            // Member 2
            psMem.setObject(1, convId);
            psMem.setObject(2, user2);
            psMem.setString(3, "member");
            psMem.executeUpdate();

            conn.commit();
            conn.setAutoCommit(true);

            Conversation c = new Conversation();
            c.setId(convId);
            c.setGroup(false);
            c.setCreatedBy(user1);
            c.setCreatedAt(LocalDateTime.now());
            return c;

        } catch (SQLException e) {
            e.printStackTrace();
            try {
                conn.rollback();
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return null;
    }
}
