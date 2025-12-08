package chatapp.dto;

import java.util.UUID;

public class ActivityUserDTO {
    private UUID id;
    private String username;
    private int numLogins;
    private int numMessages;
    private int total;

    public ActivityUserDTO() {
    }

    public ActivityUserDTO(UUID id, String username, int numLogins,
            int numMessages, int total) {
        this.id = id;
        this.username = username;
        this.numLogins = numLogins;
        this.numMessages = numMessages;
        this.total = total;
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

    public int getNumLogins() {
        return numLogins;
    }

    public void setNumLogins(int numLogins) {
        this.numLogins = numLogins;
    }

    public int getNumMessages() {
        return numMessages;
    }

    public void setNumMessages(int numMessages) {
        this.numMessages = numMessages;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
