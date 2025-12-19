package chatapp.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class ChatGroupDTO {
    private UUID id;
    private String groupName;
    private int numMember;
    private String creator;
    private LocalDateTime createdAt;

    public ChatGroupDTO() {
    }

    public ChatGroupDTO(UUID id, String groupName, int numMember, String creator, LocalDateTime createdAt) {
        this.id = id;
        this.groupName = groupName;
        this.numMember = numMember;
        this.creator = creator;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getNumMember() {
        return numMember;
    }

    public void setNumMember(int numMember) {
        this.numMember = numMember;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    @Override
    public String toString() {
        return "ChatGroupDTO{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", numMember=" + numMember +
                ", creator='" + creator + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
