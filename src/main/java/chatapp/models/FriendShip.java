package chatapp.models;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import chatapp.db.DBConnection;
import chatapp.dto.UserFriendsDTO;

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

    // query
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
        // Get friends where status is accepted. Consider both (user_id, friend_id)
        // directions if your design treats (A, B) same as (B, A).
        // Based on script.sql: check (user_id <> friend_id).
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
        // Incoming requests: user is friend_id, requester is user_id (usually).
        // requester_id column explicitly tracks who sent it.
        // So we want friendships where friend_id = userId AND status = 'pending'.
        // The user who sent it is 'requester_id' (which should match user_id if logic
        // follows).
        // Let's join with Users to get the details of the requester.
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
        // userId is the one accepting (so they are the friend_id in the row, or we just
        // find the row by both IDs)
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
        Connection conn = DBConnection.getConnection();
        // 1. Remove existing friendship if any
        removeFriend(userId, blockedId);
        // 2. Insert blocked record
        String sql = """
                    INSERT INTO friendships (user_id, friend_id, requester_id, status, created_at, updated_at)
                    VALUES (?, ?, ?, 'blocked', NOW(), NOW())
                """;
        try (java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, userId);
            ps.setObject(2, blockedId);
            ps.setObject(3, userId); // The one who blocks is the "requester" of the block action
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> searchUsers(String keyword, UUID currentUserId) {
        List<User> list = new ArrayList<>();
        Connection conn = DBConnection.getConnection();
        // Exclude users who blocked me:
        // "blocked" status: user_id blocked friend_id. requester_id is the blocker.
        // If I am blocked by X: friendship(user_id=me/X, friend_id=X/me, status='blocked', requester_id=X)
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

    // Check relationship: "friends", "pending_sent", "pending_received", "blocked", "none"
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
                
                if ("accepted".equals(status)) return "friends";
                if ("blocked".equals(status)) return "blocked"; // Either I blocked them or they blocked me (though search hides blockers)
                if ("pending".equals(status)) {
                    if (requesterV.equals(userId)) return "pending_sent";
                    else return "pending_received";
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
        user.setOnline(rs.getBoolean("is_online"));
        user.setAdmin(rs.getBoolean("admin"));
        // ... map other fields if necessary
        return user;
    }
}
