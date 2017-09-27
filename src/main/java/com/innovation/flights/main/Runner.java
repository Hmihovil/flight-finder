package com.innovation.flights.main;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64.decodeBase64;
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

        final List<String> flightNumbers = events.getItems()
                .stream()
                .map(Event::getSummary)
                .map(Runner::extractFlightNumber)
                .collect(Collectors.toList());

        System.out.println(flightNumbers);

        for (String flightNumber : flightNumbers) {
            System.out.println("flightNumber: " + flightNumber);

            final ListMessagesResponse messages = getGmailService()
                    .users()
                    .messages()
                    .list("me")
                    .setQ(flightNumber)
                    .execute();

            if (messages.getMessages() != null) {
                for (Message message : messages.getMessages()) {
                    String id = message.getId();
                    System.out.println("id: " + id);

                    final Message foundMessage = getGmailService()
                            .users()
                            .messages()
                            .get("me", id)
                            .execute();

                    if (foundMessage.getPayload() != null && foundMessage.getPayload().getParts() != null) {
                        for (MessagePart part : foundMessage.getPayload().getParts()) {
                            if (part.getFilename() != null && part.getFilename().length() > 0) {
                                String filename = part.getFilename();
                                if (filename.contains(flightNumber)) {
                                    String attId = part.getBody().getAttachmentId();

                                    MessagePartBody attachPart = getGmailService().users().messages().attachments().
                                            get("me", id, attId).execute();

                                    byte[] fileByteArray = decodeBase64(attachPart.getData());
                                    FileOutputStream fileOutFile =
                                            new FileOutputStream("/Users/dev/Downloads/" + filename);
                                    fileOutFile.write(fileByteArray);
                                    fileOutFile.close();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static String extractFlightNumber(String summary) {
        return summary.substring(summary.indexOf('(') + 1, summary.indexOf(')'));
    }
}
