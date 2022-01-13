package habiticasupport;

import com.google.gson.Gson;
import taskmanager.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class HabiticaAPIHandler {
    private final String taskJSON;

    HabiticaAPIHandler(Task task) {
        this.taskJSON = new Gson().toJson(task);
    }

    String postTask(String userId, String apiId) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://habitica.com/api/v3/tasks/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJSON))
                .setHeader("x-api-user", userId)
                .setHeader("x-api-key", apiId)
                .build();

        HttpResponse<String> response = client.send(request,
                HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
