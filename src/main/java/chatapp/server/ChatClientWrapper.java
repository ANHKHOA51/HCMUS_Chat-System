package chatapp.server;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.UUID;
import java.util.function.Consumer;

public class ChatClientWrapper extends WebSocketClient {

    private final UUID userId;
    private Consumer<UUID> onRefreshChat;
    private Consumer<UUID> onUserOnline;
    private Consumer<UUID> onUserOffline;

    public ChatClientWrapper(URI serverUri, UUID userId) {
        super(serverUri);
        this.userId = userId;
    }

    public void setOnRefreshChat(Consumer<UUID> r) {
        this.onRefreshChat = r;
    }

    public void setOnUserOnline(Consumer<UUID> o) {
        this.onUserOnline = o;
    }

    public void setOnUserOffline(Consumer<UUID> o) {
        this.onUserOffline = o;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to ChatServer");
        send("LOGIN:" + userId.toString());
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Client received: " + message);
        try {
            if (message.startsWith("REFRESH:")) {
                String senderIdStr = message.substring(8);
                if (onRefreshChat != null) {
                    onRefreshChat.accept(UUID.fromString(senderIdStr));
                }
            } else if (message.startsWith("ONLINE:")) {
                String uidStr = message.substring(7);
                if (onUserOnline != null)
                    onUserOnline.accept(UUID.fromString(uidStr));
            } else if (message.startsWith("OFFLINE:")) {
                String uidStr = message.substring(8);
                if (onUserOffline != null)
                    onUserOffline.accept(UUID.fromString(uidStr));
            } else if (message.startsWith("ONLINE_LIST:")) {
                String list = message.substring(12);
                String[] ids = list.split(",");
                for (String id : ids) {
                    if (!id.isEmpty() && onUserOnline != null) {
                        try {
                            onUserOnline.accept(UUID.fromString(id));
                        } catch (Exception e) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from ChatServer: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public void notifyUser(UUID targetId) {
        if (isOpen()) {
            send("NOTIFY:" + targetId.toString());
        }
    }
}
