package pes.CultureFinderBackend.domain.services;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.io.IOException;

public interface INotificationService {

    void initializeFirebase() throws IOException;
    void sendNotification(String userId, String title, String body);
    void sendAllNotifications(String apiToken);
    void sendNearEventsNotification(String userId);
}
