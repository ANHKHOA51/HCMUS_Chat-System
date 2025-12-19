package chatapp.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

public class ChatCache {
    // Thread-safe map
    private static final Map<UUID, List<Message>> cache = Collections.synchronizedMap(new HashMap<>());

    public static List<Message> get(UUID conversationId) {
        if (conversationId == null)
            return null;
        return cache.get(conversationId);
    }

    public static void put(UUID conversationId, List<Message> messages) {
        if (conversationId == null)
            return;
        // Store a copy to prevent external modification issues if needed, but for
        // performance we might store direct ref
        // Let's store a defensive copy usually, but here simplicity wins.
        cache.put(conversationId, new ArrayList<>(messages));
    }

    public static void add(UUID conversationId, Message message) {
        if (conversationId == null || message == null)
            return;
        cache.computeIfPresent(conversationId, (k, list) -> {
            list.add(message);
            return list;
        });
        // If not present, we don't add because we might have a partial state.
        // Better to fetch all if missing. So computeIfPresent is correct.
    }

    public static void invalidate(UUID conversationId) {
        if (conversationId == null)
            return;
        cache.remove(conversationId);
    }

    public static void clearAll() {
        cache.clear();
    }
}
