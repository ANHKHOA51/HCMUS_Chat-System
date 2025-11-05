package chatapp.models;

public class ChatGroup {
    private int id;
    private String groupName;
    private int numMember;
    private String createdAt;

    public ChatGroup(int id, String groupName, int numMember, String createdAt) {
        this.id = id;
        this.groupName = groupName;
        this.numMember = numMember;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getNumMember() {
        return numMember;
    }

    public String getCreatedAt() {
        return createdAt;
    }

}
