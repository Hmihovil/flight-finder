package com.innovation.flights.main;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.gmail.model.Label;
import com.google.api.services.gmail.model.ListLabelsResponse;

import java.io.IOException;
import java.util.List;

import static com.innovation.flights.service.CalendarServiceFactory.getCalendarService;
import static com.innovation.flights.service.GmailServiceFactory.getGmailService;

public class Runner {

    private static final String FLIGHTS_CALENDAR_ID = "quassr9esqvt5d2pag09m6tdqg@group.calendar.google.com";

    public static void main(String[] args) throws IOException {
        Events events = getCalendarService().events()
                .list(FLIGHTS_CALENDAR_ID)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        events.getItems()
                .stream()
                .map(Event::getSummary)
                .map(Runner::extractFlightNumber)
                .forEach(System.out::println);

        String user = "me";
        ListLabelsResponse listResponse =
                getGmailService().users().labels().list(user).execute();
        List<Label> labels = listResponse.getLabels();
        if (labels.size() == 0) {
            System.out.println("No labels found.");
        } else {
            System.out.println("Labels:");
            for (Label label : labels) {
                System.out.printf("- %s\n", label.getName());
            }
        }
    }

    private static String extractFlightNumber(String summary) {
        return summary.substring(summary.indexOf('(') + 1, summary.indexOf(')'));
    }
}
