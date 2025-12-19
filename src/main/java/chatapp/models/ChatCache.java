package chatapp.models;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

public class ChatCache {
    private static final Map<UUID, List<Message>> cache = Collections.synchronizedMap(new HashMap<>());

    public static List<Message> get(UUID conversationId) {
        if (conversationId == null)
            return null;
        return cache.get(conversationId);
    }

    public static void put(UUID conversationId, List<Message> messages) {
        if (conversationId == null)
            return;

        cache.put(conversationId, new ArrayList<>(messages));
    }

    public static void add(UUID conversationId, Message message) {
        if (conversationId == null || message == null)
            return;
        cache.computeIfPresent(conversationId, (k, list) -> {
            list.add(message);
            return list;
        });
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
