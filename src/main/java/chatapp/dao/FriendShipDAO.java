package chatapp.dao;

import chatapp.db.DBConnection;
import chatapp.dto.UserFriendsDTO;
import chatapp.models.User;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FriendShipDAO {

    public static List<UserFriendsDTO> getListUserFriends() {
        List<UserFriendsDTO> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                SELECT
                    u.id AS id,
                    u.username AS username,
                    u.display_name AS display_name,
                    u.email AS email,
                    COUNT(DISTINCT
                        CASE
                            WHEN f.user_id = u.id THEN f.friend_id
                            WHEN f.friend_id = u.id THEN f.user_id
                        END
                    ) AS num_friends,
                    COUNT(DISTINCT
                        CASE
                            WHEN fof.user_id = u.id THEN fof.friend_id
                            ELSE fof.user_id
                        END
                    ) AS num_friends_of_friend
                FROM users u
                LEFT JOIN friendships f
                    ON (f.user_id = u.id OR f.friend_id = u.id) AND f.status = 'accepted'
                LEFT JOIN friendships fof
                    ON (
                            (fof.user_id = f.user_id OR fof.friend_id = f.user_id)
                        OR (fof.user_id = f.friend_id OR fof.friend_id = f.friend_id)
                    )
                    AND fof.user_id != u.id
                    AND fof.friend_id != u.id
                    AND fof.status = 'accepted'
                GROUP BY u.id, u.username;
                    """;

        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                UserFriendsDTO item = new UserFriendsDTO();
                item.setId(UUID.fromString(rs.getString("id")));
                item.setUsername(rs.getString("username"));
                item.setDisplayName(rs.getString("display_name"));
                item.setEmail(rs.getString("email"));
                item.setNumFriends(rs.getInt("num_friends"));
                item.setNumFriendsOfFriends(rs.getInt("num_friends_of_friend"));

                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    public static List<User> getFriendsList(UUID userId) {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                    SELECT u.*
                    FROM users u
                    JOIN friendships f ON (f.user_id = u.id OR f.friend_id = u.id)
                    WHERE (f.user_id = ? OR f.friend_id = ?)
                    AND u.id != ?
                    AND f.status = 'accepted'
                """;

        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, userId);
            ps.setObject(3, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<User> getPendingRequests(UUID userId) {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                    SELECT u.*
                    FROM users u
                    JOIN friendships f ON f.requester_id = u.id
                    WHERE f.friend_id = ?
                    AND f.status = 'pending'
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean sendFriendRequest(UUID requesterId, UUID targetId) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                    INSERT INTO friendships (user_id, friend_id, requester_id, status, created_at, updated_at)
                    VALUES (?, ?, ?, 'pending', NOW(), NOW())
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, requesterId);
            ps.setObject(2, targetId);
            ps.setObject(3, requesterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean acceptFriendRequest(UUID userId, UUID requesterId) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                    UPDATE friendships
                    SET status = 'accepted', accepted_at = NOW(), updated_at = NOW()
                    WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)
                    AND status = 'pending'
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, requesterId);
            ps.setObject(2, userId);
            ps.setObject(3, userId);
            ps.setObject(4, requesterId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeFriend(UUID userId, UUID friendId) {
        Connection conn = DBConnection.getConnection();
        String sql = "DELETE FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, friendId);
            ps.setObject(3, friendId);
            ps.setObject(4, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean blockUser(UUID userId, UUID blockedId) {
        removeFriend(userId, blockedId);
        Connection conn = DBConnection.getConnection();
        String sql = """
                    INSERT INTO friendships (user_id, friend_id, requester_id, status, created_at, updated_at)
                    VALUES (?, ?, ?, 'blocked', NOW(), NOW())
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, blockedId);
            ps.setObject(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean unblockUser(UUID userId, UUID blockedId) {
        Connection conn = DBConnection.getConnection();
        String sql = """
                    DELETE FROM friendships
                    WHERE user_id = ? AND friend_id = ? AND status = 'blocked'
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, blockedId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> searchUsers(String keyword, UUID currentUserId) {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        String sql = """
                    SELECT u.*
                    FROM users u
                    WHERE (u.username ILIKE ? OR u.display_name ILIKE ?)
                    AND u.id != ?
                    AND NOT EXISTS (
                        SELECT 1 FROM friendships f
                        WHERE (
                            (f.user_id = u.id AND f.friend_id = ?)
                            OR
                            (f.user_id = ? AND f.friend_id = u.id)
                        )
                        AND f.status = 'blocked'
                        AND f.requester_id = u.id
                    )
                """;

        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setObject(3, currentUserId);
            ps.setObject(4, currentUserId);
            ps.setObject(5, currentUserId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static String getRelationship(UUID userId, UUID targetId) {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT status, requester_id FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
        try {
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            ps.setObject(1, userId);
            ps.setObject(2, targetId);
            ps.setObject(3, targetId);
            ps.setObject(4, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                UUID requesterV = UUID.fromString(rs.getString("requester_id"));

                if ("accepted".equals(status))
                    return "friends";
                if ("blocked".equals(status))
                    return "blocked";
                if ("pending".equals(status)) {
                    if (requesterV.equals(userId))
                        return "pending_sent";
                    else
                        return "pending_received";
                }
                return status;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "none";
    }

    private static User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setUsername(rs.getString("username"));
        user.setDisplayName(rs.getString("display_name"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setPassword(rs.getString("password"));
        user.setGender(rs.getBoolean("gender"));
        user.setAdmin(rs.getBoolean("admin"));
        user.setOnline(rs.getBoolean("is_online"));
        user.setLock(rs.getBoolean("lock"));
        user.setBirthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null);
        user.setCreatedAt(
                rs.getTimestamp("created_at") != null ? rs.getTimestamp("created_at").toLocalDateTime() : null);
        user.setUpdatedAt(
                rs.getTimestamp("updated_at") != null ? rs.getTimestamp("updated_at").toLocalDateTime() : null);
        return user;
    }
}
