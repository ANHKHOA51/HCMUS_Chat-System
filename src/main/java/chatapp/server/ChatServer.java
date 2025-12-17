package chatapp.server;

import org.java_websocket.server.WebSocketServer;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ChatServer extends WebSocketServer {

    // Map to store active connections: UserID -> WebSocket
    private final Map<UUID, WebSocket> clientConnections = Collections.synchronizedMap(new HashMap<>());
    // Map to store reverse lookup: WebSocket -> UserID
    private final Map<WebSocket, UUID> connectionToUser = Collections.synchronizedMap(new HashMap<>());

    // Set to store Admin Monitor connections
    private final Set<WebSocket> adminConnections = Collections.synchronizedSet(new HashSet<>());

    public ChatServer(InetSocketAddress address) {
        super(address);
    }

    public static void main(String[] args) {
        String host = chatapp.db.DBConnection.get("CHAT_SERVER_HOST", "0.0.0.0");
        int port = Integer.parseInt(chatapp.db.DBConnection.get("CHAT_SERVER_PORT", "8887"));
        ChatServer server = new ChatServer(new InetSocketAddress(host, port));
        server.start();
        System.out.println("ChatServer started on port: " + port);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("New connection: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        System.out.println("Closed connection: " + conn.getRemoteSocketAddress() + " Exit: " + reason);
        UUID userId = connectionToUser.remove(conn);
        if (userId != null) {
            clientConnections.remove(userId);
            broadcast("OFFLINE:" + userId.toString());
            broadcastActiveUsersToAdmins(); // Update admins
        }
        adminConnections.remove(conn);
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Received: " + message);
        try {
            // Protocol:
            // LOGIN:<userId>
            // NOTIFY:<targetId> (User A notifications User B)
            // ADMIN_MONITOR (Admin registers to receive updates)

            if (message.startsWith("LOGIN:")) {
                String userIdStr = message.substring(6);
                UUID userId = UUID.fromString(userIdStr);

                // Remove existing connection if any
                WebSocket existing = clientConnections.get(userId);
                if (existing != null && existing.isOpen()) {
                    existing.close();
                }

                clientConnections.put(userId, conn);
                connectionToUser.put(conn, userId);

                // Notify this user about all online users
                StringBuilder onlineList = new StringBuilder("ONLINE_LIST:");
                synchronized (clientConnections) {
                    for (UUID id : clientConnections.keySet()) {
                        onlineList.append(id).append(",");
                    }
                }
                conn.send(onlineList.toString());

                // Broadcast this user is ONLINE
                broadcast("ONLINE:" + userId.toString());
                broadcastActiveUsersToAdmins(); // Update admins

            } else if (message.equals("ADMIN_MONITOR")) {
                adminConnections.add(conn);
                System.out.println("Admin Monitor registered: " + conn.getRemoteSocketAddress());
                // Send current list immediately
                sendActiveUsersTo(conn);

            } else if (message.startsWith("NOTIFY:")) {
                String targetIdStr = message.substring(7);
                UUID senderId = connectionToUser.get(conn);
                if (senderId == null)
                    return; // Not logged in

                UUID targetId = UUID.fromString(targetIdStr);
                WebSocket targetConn = clientConnections.get(targetId);

                if (targetConn != null && targetConn.isOpen()) {
                    targetConn.send("REFRESH:" + senderId.toString());
                    System.out.println("Sent REFRESH to " + targetId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void broadcastActiveUsersToAdmins() {
        if (adminConnections.isEmpty())
            return;

        StringBuilder userList = new StringBuilder("ACTIVE_USERS:");
        synchronized (clientConnections) {
            for (UUID id : clientConnections.keySet()) {
                userList.append(id).append(",");
            }
        }
        String msg = userList.toString();

        synchronized (adminConnections) {
            for (WebSocket admin : adminConnections) {
                if (admin.isOpen()) {
                    admin.send(msg);
                }
            }
        }
    }

    private void sendActiveUsersTo(WebSocket conn) {
        StringBuilder userList = new StringBuilder("ACTIVE_USERS:");
        synchronized (clientConnections) {
            for (UUID id : clientConnections.keySet()) {
                userList.append(id).append(",");
            }
        }
        conn.send(userList.toString());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("Error:");
        ex.printStackTrace();
    }

    @Override
    public void onStart() {
        System.out.println("Server started successfully!");
    }
}
