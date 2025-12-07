package chatapp.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Conversation {
    private UUID id;
    private UUID createdBy;
    private boolean group;
    private String title;
    private LocalDateTime createdAt;

    public Conversation() {
    }

    public Conversation(UUID id, UUID createdBy, boolean group, String title, LocalDateTime createdAt) {
        this.id = id;
        this.createdBy = createdBy;
        this.group = group;
        this.title = title;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isGroup() {
        return group;
    }

    public void setGroup(boolean group) {
        this.group = group;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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
        return "Conversation{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", group=" + group +
                ", title='" + title + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
