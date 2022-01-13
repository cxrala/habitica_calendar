package habiticasupport;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import io.github.furstenheim.CopyDown;
import taskmanager.Priorities;
import taskmanager.Task;
import taskmanager.Types;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class HabiticaEventSyncer {

    List<Event> events;

    public HabiticaEventSyncer(List<Event> events) {
        this.events = events;
    }

    public String sendEvents(String userId, String apiId) throws IOException, InterruptedException {

        if (events.isEmpty()) {
            return "No events sent as calendar empty.";
        } else {
            for (Event event : events) {
                // get title of event
                String text = event.getSummary();

                // todo: encapsulate this logic outside/use some sort of interface

                // get start and end times
                DateTime start = event.getStart().getDateTime();
                DateTime end = event.getEnd().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate();
                }
                if (end == null) {
                    end = event.getEnd().getDate();
                }

                DateFormat formatter = new SimpleDateFormat("HH:mm");
                formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date dateStart = new Date(start.getValue());
                String startDateFormatted = formatter.format(dateStart);
                Date dateEnd = new Date(end.getValue());
                String endDateFormatted = formatter.format(dateEnd);

                String eventName = String.format("%s - %s | %s", startDateFormatted, endDateFormatted, text);

                CopyDown converter = new CopyDown();

                // todo: encapsulate above logic

                Task task = new Task.Builder(eventName, Types.TODO)
                        .setPriority(Priorities.MEDIUM)
                        .setNotes(event.getDescription() != null ? converter.convert(event.getDescription()) : "")
                        .build();

                HabiticaAPIHandler handler = new HabiticaAPIHandler(task);
                System.out.println("INFO | " + handler.postTask(userId, apiId));
            }
        }
        return "Success";
    }
}
