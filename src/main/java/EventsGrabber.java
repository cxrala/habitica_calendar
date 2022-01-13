import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

public class EventsGrabber {
    Calendar service;
    DateTime min;
    DateTime max;

    EventsGrabber(Calendar service, int days) {
        this.service = service;

        java.util.Calendar date = new GregorianCalendar();
        date.set(java.util.Calendar.HOUR_OF_DAY, 0);
        date.set(java.util.Calendar.MINUTE, 0);
        date.set(java.util.Calendar.SECOND, 0);
        date.set(java.util.Calendar.MILLISECOND, 0);

        min = new DateTime(date.getTimeInMillis());
        date.add(java.util.Calendar.DAY_OF_MONTH, days);
        max = new DateTime(date.getTimeInMillis());
    }

    EventsGrabber(Calendar service) {
        this(service, 1);
    }

    List<Event> getEvents() throws IOException {
        List<Event> todayEvents = new ArrayList<>();
        String pageToken = null;
        do {
            CalendarList calendarList = service.calendarList().list().setPageToken(pageToken).execute();
            List<CalendarListEntry> items = calendarList.getItems();

            for (CalendarListEntry calendarListEntry : items) {
                Events events = service.events().list(calendarListEntry.getId())
                        .setTimeMin(min)
                        .setTimeMax(max)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                todayEvents.addAll(events.getItems());
            }
            pageToken = calendarList.getNextPageToken();
        } while (pageToken != null);

        todayEvents.sort(new EventComparator());

        return todayEvents;
    }
}
