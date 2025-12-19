package chatapp.models;

public class GroupUser extends User {
    public GroupUser(Conversation conversation) {
        super();
        this.setId(conversation.getId());
        this.setUsername(conversation.getTitle());
        this.setDisplayName(conversation.getTitle());
        this.setGender(false);
        this.setOnline(true);
    }
}
