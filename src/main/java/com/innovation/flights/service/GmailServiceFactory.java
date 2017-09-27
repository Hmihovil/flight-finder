package com.innovation.flights.service;

import com.google.api.services.gmail.Gmail;

import java.io.IOException;

import static com.innovation.flights.service.GoogleServiceFactoryCredentials.*;

public class GmailServiceFactory {
    /**
     * Build and return an authorized Gmail client service.
     *
     * @return an authorized Gmail client service
     * @throws IOException
     */
    public static Gmail getGmailService() throws IOException {
        return new Gmail.Builder(httpTransport(), jsonFactory(), getCredentials())
                .setApplicationName(applicationName())
                .build();
    }
}
