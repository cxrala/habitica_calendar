package habiticasupport;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import events.EventsGrabber;
import habiticasupport.tags.TagGenerator;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

public class HabiticaUpdater {
    String userId;
    String apiId;
    Calendar service;
    Set<String> tasks;

    public HabiticaUpdater(String userId, String apiId, Calendar service) {
        this.userId = userId;
        this.apiId = apiId;
        this.service = service;
        this.tasks = new HashSet<>();
    }

    public void update(boolean fromStart) throws IOException, InterruptedException {
        String tag = "daily_planner";
        clear(TagGenerator.getTagGenerator(userId, apiId).getUUID("daily_planner"));

        EventsGrabber events = new EventsGrabber(service, fromStart);
        List<Event> todayEvents = events.getEvents();
        HabiticaEventSyncer syncer = new HabiticaEventSyncer(todayEvents);

        syncer.sendEvents(userId, apiId, tag);
    }

    private void clear(String tag) throws IOException, InterruptedException {
        fillTasks(tag);

        HttpClient client = HttpClient.newHttpClient();
        for (var task:tasks) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://habitica.com/api/v3/tasks/" + task))
                    .DELETE()
                    .setHeader("x-api-user", userId)
                    .setHeader("x-api-key", apiId)
                    .build();

            HttpResponse<String> response = client.send(request,
                    HttpResponse.BodyHandlers.ofString());
            Thread.sleep(2000);
            System.out.println("INFO | DELETED " + response.body());
        }

    }

    private void fillTasks(String requiredTag) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://habitica.com/api/v3/tasks/user?type=todos"))
                .setHeader("x-api-user", userId)
                .setHeader("x-api-key", apiId)
                .build();

        String jsonStr = client.send(request, HttpResponse.BodyHandlers.ofString()).body();
        JsonObject json = new Gson().fromJson(jsonStr, JsonObject.class);

        JsonArray taskData = json.getAsJsonArray("data");

        for (var task:taskData) {
            JsonObject currentTask = new Gson().fromJson(task, JsonObject.class);
            String taskId = currentTask.get("_id").getAsString();
            JsonArray tagIds = currentTask.get("tags").getAsJsonArray();
            for (var tag:tagIds) {
                String tagId = tag.getAsString();
                if (tagId.equals(requiredTag)) {
                    tasks.add(taskId);
                    break;
                }
            }
        }
    }
}
