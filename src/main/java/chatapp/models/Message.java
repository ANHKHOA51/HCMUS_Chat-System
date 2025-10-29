package chatapp.models;


import java.time.LocalDateTime;

public class Message {
    private String sender;
    private String reciever;
    private String msg_content;
    private LocalDateTime time; 
    private boolean isRead;
}
