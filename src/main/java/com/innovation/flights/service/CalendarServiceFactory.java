package com.innovation.flights.service;

import com.google.api.services.calendar.Calendar;

import java.io.IOException;

import static com.innovation.flights.service.GoogleServiceFactoryCredentials.*;

public class CalendarServiceFactory {
    public static Calendar getCalendarService() throws IOException {
        return new Calendar.Builder(httpTransport(), jsonFactory(), getCredentials())
                .setApplicationName(applicationName())
                .build();
    }
}
