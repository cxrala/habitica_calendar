package events;

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
    private final Calendar service;
    private final DateTime min;
    private final DateTime max;

    //todo: boolean to determine whether from start of day or now
    EventsGrabber(Calendar service, int days, boolean fromStart) {
        this.service = service;

        java.util.Calendar date = new GregorianCalendar();
        date.set(java.util.Calendar.HOUR_OF_DAY, 0);
        date.set(java.util.Calendar.MINUTE, 0);
        date.set(java.util.Calendar.SECOND, 0);
        date.set(java.util.Calendar.MILLISECOND, 0);

        if (fromStart) {
            min = new DateTime(date.getTimeInMillis());
        } else {
            min = new DateTime(System.currentTimeMillis());
        }

        date.add(java.util.Calendar.DAY_OF_MONTH, days);
        max = new DateTime(date.getTimeInMillis());

    }

    public EventsGrabber(Calendar service, boolean fromStart) {
        this(service, 1, fromStart);
    }

    public List<Event> getEvents() throws IOException {
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

        todayEvents.sort((new EventComparator()).reversed());

        return todayEvents;
    }
}
