package chatapp.models;

public class GroupMember extends User {
    private String role; // "admin" or "member"

    public GroupMember() {
        super();
    }

    public GroupMember(User user, String role) {
        super();
        this.setId(user.getId());
        this.setUsername(user.getUsername());
        this.setDisplayName(user.getDisplayName());
        this.setGender(user.isGender());
        this.setOnline(user.isOnline());
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
