package chatapp.dao;

import chatapp.db.DBConnection;
import chatapp.dto.ChatGroupDTO;
import chatapp.models.Conversation;
import chatapp.models.User;
import chatapp.models.GroupMember;

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

    public static Conversation createChatGroup(String groupName, UUID creatorId, List<UUID> memberIds) {
        Connection conn = DBConnection.getConnection();
        try {
            conn.setAutoCommit(false);

            // 1. Create Conversation
            UUID convId = UUID.randomUUID();
            String sqlConv = "INSERT INTO conversations (id, title, isGroup, created_by, created_at) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement psConv = conn.prepareStatement(sqlConv);
            psConv.setObject(1, convId);
            psConv.setString(2, groupName);
            psConv.setBoolean(3, true);
            psConv.setObject(4, creatorId);
            psConv.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            psConv.executeUpdate();

            // 2. Add Creator as Admin
            String sqlMem = "INSERT INTO conversation_members (conversation_id, user_id, role) VALUES (?, ?, ?)";
            PreparedStatement psMem = conn.prepareStatement(sqlMem);

            psMem.setObject(1, convId);
            psMem.setObject(2, creatorId);
            psMem.setString(3, "admin");
            psMem.addBatch();

            // 3. Add Members
            for (UUID memberId : memberIds) {
                psMem.setObject(1, convId);
                psMem.setObject(2, memberId);
                psMem.setString(3, "member");
                psMem.addBatch();
            }
            psMem.executeBatch();

            conn.commit();
            conn.setAutoCommit(true);

            Conversation c = new Conversation();
            c.setId(convId);
            c.setGroup(true);
            c.setTitle(groupName);
            c.setCreatedBy(creatorId);
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

    public static Conversation getConversation(UUID id) {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT * FROM conversations WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Conversation c = new Conversation();
                c.setId(UUID.fromString(rs.getString("id")));
                c.setTitle(rs.getString("title"));
                c.setGroup(rs.getBoolean("isGroup"));
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

    public static List<UUID> getConversationMemberIds(UUID conversationId) {
        List<UUID> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT user_id FROM conversation_members WHERE conversation_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(UUID.fromString(rs.getString("user_id")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<chatapp.models.GroupUser> getGroupsForUser(UUID userId) {
        List<chatapp.models.GroupUser> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT c.*
                FROM conversations c
                JOIN conversation_members cm ON c.id = cm.conversation_id
                WHERE c.isGroup = TRUE AND cm.user_id = ?
                """;
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                chatapp.models.Conversation c = new Conversation();
                c.setId(UUID.fromString(rs.getString("id")));
                c.setTitle(rs.getString("title"));
                c.setGroup(true);
                // c.setCreatedBy(...) // Optional

                chatapp.models.GroupUser gu = new chatapp.models.GroupUser(c);
                list.add(gu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<GroupMember> getGroupMembers(UUID conversationId) {
        List<GroupMember> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT u.*, cm.role
                FROM conversation_members cm
                JOIN users u ON u.id = cm.user_id
                WHERE cm.conversation_id = ?
                ORDER BY cm.role ASC, u.username ASC
                """; // Role ASC usually puts 'admin' before 'member' alphabetically

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User u = new User();
                u.setId(UUID.fromString(rs.getString("id")));
                u.setUsername(rs.getString("username"));
                u.setDisplayName(rs.getString("display_name"));
                // u.setGender(rs.getBoolean("gender")); // Assume not critical for list
                u.setOnline(rs.getBoolean("is_online"));

                GroupMember gm = new GroupMember(u, rs.getString("role"));
                list.add(gm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean renameConversation(UUID conversationId, String newName) {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE conversations SET title = ? WHERE id = ? AND isGroup = TRUE";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newName);
            ps.setObject(2, conversationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean addMember(UUID conversationId, UUID userId) {
        Connection conn = DBConnection.getConnection();
        // Check if already member?
        String sql = "INSERT INTO conversation_members (conversation_id, user_id, role) VALUES (?, ?, 'member') ON CONFLICT DO NOTHING";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeMember(UUID conversationId, UUID userId) {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM conversation_members WHERE conversation_id = ? AND user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ps.setObject(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateMemberRole(UUID conversationId, UUID userId, String newRole) {
        Connection conn = DBConnection.getConnection();
        String sql = "UPDATE conversation_members SET role = ? WHERE conversation_id = ? AND user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newRole);
            ps.setObject(2, conversationId);
            ps.setObject(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isAdmin(UUID conversationId, UUID userId) {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT role FROM conversation_members WHERE conversation_id = ? AND user_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, conversationId);
            ps.setObject(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return "admin".equals(rs.getString("role"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
