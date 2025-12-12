package chatapp.models;

public class GroupUser extends User {
    public GroupUser(Conversation conversation) {
        super();
        this.setId(conversation.getId());
        this.setUsername(conversation.getTitle());
        this.setDisplayName(conversation.getTitle());
        // Set other fields to defaults or special values indicating it's a group
        this.setGender(false);
        this.setOnline(true); // Groups are always "online"?
    }
}
