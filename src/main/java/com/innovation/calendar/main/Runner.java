package com.innovation.calendar.main;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Events;
import com.google.common.collect.ImmutableSet;

import java.io.IOException;
import java.util.Set;

import static com.innovation.calendar.service.CalendarServiceFactory.getCalendarService;

public class Runner {

    private static final Set<String> FLIGHT_WORDS = ImmutableSet.of("flight");

    public static void main(String[] args) throws IOException {

        Events events = getCalendarService().events()
                .list("primary")
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        events.getItems()
                .stream()
                .map(event -> CalendarEvent.of(event.getSummary().toLowerCase(), event.getStart().getDateTime()))
                .filter(CalendarEvent::isFlightEvent)
                .forEach(System.out::println);
    }

    private static class CalendarEvent {
        private final String summary;
        private final DateTime dateTime;

        CalendarEvent(final String summary, final DateTime dateTime) {
            this.summary = summary;
            this.dateTime = dateTime;
        }

        boolean isFlightEvent() {
            return FLIGHT_WORDS
                    .stream()
                    .anyMatch(summary::contains);
        }

        @Override
        public String toString() {
            return "CalendarEvent{" +
                    "summary='" + summary + '\'' +
                    ", dateTime=" + dateTime +
                    '}';
        }

        static CalendarEvent of(final String summary, final DateTime dateTime) {
            return new CalendarEvent(summary, dateTime);
        }
    }
}
