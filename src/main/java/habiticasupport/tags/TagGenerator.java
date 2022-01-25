package habiticasupport.tags;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

public class TagGenerator {
    private final String userId;
    private final String apiId;
    private final Map<String, String> tagMap;

    private TagGenerator(String userId, String apiId, Map<String, String> tagMap) {
        this.tagMap = tagMap;
        this.userId = userId;
        this.apiId = apiId;
    }

    public static TagGenerator getTagGenerator(String userId, String apiId) throws IOException, InterruptedException {
        Map<String, String> tagMap = new HashMap<>();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://habitica.com/api/v3/tags"))
                .setHeader("x-api-user", userId)
                .setHeader("x-api-key", apiId)
                .build();

        String jsonStr = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
        JsonArray tagData = json.getAsJsonArray("data");

        for (int i = 0; i < tagData.size(); i++) {
            JsonObject currentTag = new Gson().fromJson(tagData.get(i), JsonObject.class);
            tagMap.put(currentTag.get("name").getAsString(), currentTag.get("id").getAsString());
        }

        return new TagGenerator(userId, apiId, tagMap);
    }

    private boolean checkTagExists(String name) {
        return tagMap.containsKey(name);
    }

    public String getUUID(String name) throws IOException, InterruptedException {
        if (checkTagExists(name)) {
            return tagMap.get(name);
        }

        return createNewTag(name);

    }

    // returns the UUID of the new tag
    private String createNewTag(String name) throws IOException, InterruptedException {
        String nameJson = String.format("{\"name\": \"%s\"}", name);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://habitica.com/api/v3/tags"))
                .header("Content-Type", "application/json")
                .setHeader("x-api-user", userId)
                .setHeader("x-api-key", apiId)
                .POST(HttpRequest.BodyPublishers.ofString(nameJson))
                .build();

        String jsonStr = client.send(request, HttpResponse.BodyHandlers.ofString()).body();

        JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);
        JsonArray tagData = json.getAsJsonArray("data");
        JsonObject createdTag = new Gson().fromJson(tagData.get(0), JsonObject.class);

        return createdTag.get("id").getAsString();

    }
}