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

public class Friendship {
    private UUID id;
    private UUID userId;
    private UUID friendId;
    private UUID requesterId;
    private String status;
    private LocalDateTime acceptedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Friendship() {
    }

    public Friendship(UUID id, UUID userId, UUID friendId, UUID requesterId,
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
}
