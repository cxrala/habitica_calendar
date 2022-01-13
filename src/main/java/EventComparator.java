import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;

import java.util.Comparator;

public class EventComparator implements Comparator<Event> {
    @Override
    public int compare(Event e1, Event e2) {
        long e1Start = e1.getStart().getDateTime().getValue();
        long e2Start = e2.getStart().getDateTime().getValue();

        if (e1Start < e2Start) {
            return -1;
        } else if (e1Start > e2Start) {
            return 1;
        }
        return 0;
    }
}
