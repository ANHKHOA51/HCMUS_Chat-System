package chatapp.dto;

import java.util.UUID;

public class UserFriendsDTO {
    private UUID id;
    private String username;
    private String displayName;
    private String email;
    private int numFriends;
    private int numFriendsOfFriends;

    public UserFriendsDTO() {
    }

    // Constructor đầy đủ
    public UserFriendsDTO(UUID id, String username, String displayName, String email,
            int numFriends, int numFriendsOfFriends) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.numFriends = numFriends;
        this.numFriendsOfFriends = numFriendsOfFriends;
    }

    // Getters & Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getNumFriends() {
        return numFriends;
    }

    public void setNumFriends(int numFriends) {
        this.numFriends = numFriends;
    }

    public int getNumFriendsOfFriends() {
        return numFriendsOfFriends;
    }

    public void setNumFriendsOfFriends(int numFriendsOfFriends) {
        this.numFriendsOfFriends = numFriendsOfFriends;
    }
}
