package chatapp.server;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

public class AdminSocketClient extends WebSocketClient {

    private Consumer<Set<UUID>> onActiveUsersUpdate;

    public AdminSocketClient(URI serverUri) {
        super(serverUri);
    }

    public void setOnActiveUsersUpdate(Consumer<Set<UUID>> callback) {
        this.onActiveUsersUpdate = callback;
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Admin Monitor connected to ChatServer");
        send("ADMIN_MONITOR");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("Admin Monitor received: " + message);
        try {
            if (message.startsWith("ACTIVE_USERS:")) {
                String listStr = message.substring(13); // Length of "ACTIVE_USERS:"
                String[] ids = listStr.split(",");
                Set<UUID> activeUsers = new HashSet<>();
                for (String id : ids) {
                    if (!id.trim().isEmpty()) {
                        try {
                            activeUsers.add(UUID.fromString(id.trim()));
                        } catch (IllegalArgumentException e) {
                            // Ignore invalid UUIDs
                        }
                    }
                }
                if (onActiveUsersUpdate != null) {
                    onActiveUsersUpdate.accept(activeUsers);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Admin Monitor disconnected: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        System.out.println("Admin Monitor Error: " + ex.getMessage());
    }
}
