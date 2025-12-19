package chatapp.utils;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import io.github.cdimascio.dotenv.Dotenv;
import org.json.JSONArray;
import org.json.JSONObject;

public class OpenRouterService {
    private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";
    private static final String MODEL = "openai/gpt-3.5-turbo"; 

    private String apiKey;

    public OpenRouterService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("OPENROUTER_API_KEY");
        if (this.apiKey == null || this.apiKey.isEmpty()) {
            System.err.println("OPENROUTER_API_KEY not found in .env");
        }
    }

    public CompletableFuture<String> getSuggestion(String conversationContext) {
        if (apiKey == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("API Key missing"));
        }

        JSONObject userMessage = new JSONObject();
        userMessage.put("role", "user");
        userMessage.put("content", "Here is a chat conversation conversation:\n" + conversationContext +
                "\n\nSuggest a short, relevant reply for me (the last speaker, or to continue the flow):");

        JSONArray messages = new JSONArray();
        messages.put(userMessage);

        JSONObject requestBody = new JSONObject();
        requestBody.put("model", MODEL);
        requestBody.put("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

        return HttpClient.newHttpClient().sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            JSONObject json = new JSONObject(response.body());
                            return json.getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content")
                                    .trim();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to parse response: " + e.getMessage());
                        }
                    } else {
                        throw new RuntimeException("API Error: " + response.statusCode() + " " + response.body());
                    }
                });
    }
}
