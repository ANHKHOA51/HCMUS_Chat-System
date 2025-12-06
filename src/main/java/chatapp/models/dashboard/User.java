package chatapp.models.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class User {
    private UUID id;
    private String username;
    private String displayName;
    private String email;
    private String address;
    private String password;
    private boolean gender;
    private boolean admin;
    private boolean online;
    private LocalDate birthday;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User() {
    }

    public User(
            UUID id, String username, String displayName, String email, String address, String password,
            boolean gender, boolean admin, boolean online,
            LocalDate birthday, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.displayName = displayName;
        this.email = email;
        this.address = address;
        this.password = password;
        this.gender = gender;
        this.admin = admin;
        this.online = online;
        this.birthday = birthday;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "User {" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", displayName='" + displayName + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", gender=" + gender +
                ", admin=" + admin +
                ", online=" + online +
                ", birthday=" + birthday +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}