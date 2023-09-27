package pes.CultureFinderBackend.domain.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pes.CultureFinderBackend.domain.exceptions.ObjectNotFoundException;
import pes.CultureFinderBackend.domain.exceptions.PermissionDeniedException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
public class NotificationService implements INotificationService {

    /*
    * Descripció: Instància del servei que s'encarrega d'interactuar amb els usuaris
    */
    @Autowired
    private IDomainUserService iDomainUserService;

    /*
    * Descripció: Instància del servei que s'encarrrega de la encriptació de JWTs
    */
    @Autowired
    private ISecurityService iSecurityService;

    /*
    * Descripció: Inicialitza els permissos per a que l'aplicació backend pugui interactuar amb Firebase
    */
    public void initializeFirebase() throws IOException {
        InputStream serviceAccount = null;
        try {
            serviceAccount =
                    new FileInputStream("./src/main/resources/config/firebase/culturefinder-3a452-firebase-adminsdk-s199h-b3f30b3c0c.json");
        } catch (IOException ignored) {
            serviceAccount =
                    getClass().getResourceAsStream("/BOOT-INF/classes/config/firebase/culturefinder-3a452-firebase-adminsdk-s199h-b3f30b3c0c.json");
        }

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }

    /*
    * Descripció: Envia una notifiació a l'usuari especificat, amb el títol i missatge(body) corresponents
    */
    public void sendNotification(String userId, String title, String body) {
        if (!iDomainUserService.existsById(userId)) throw new ObjectNotFoundException("User not found");
        String deviceToken = iDomainUserService.getDeviceToken(userId);

        Message message = Message.builder()
                .setToken(deviceToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Error sending all notifications");
        }
    }


    /*
    * Descripció: Envia totes les notificacions del sistema a un usuari en qüestió
    */
    public void sendAllNotifications(String apiToken) {
        String adminId = (String) iSecurityService.buildMappedObjectFromApiToken(apiToken).get("userId");
        if (!iDomainUserService.isAdmin(adminId)) throw new PermissionDeniedException("User " + adminId + " is not allowed to send all notifications!");
        List<String> loggedUsers = iDomainUserService.getAllLoggedUserIds();
        for (String userId : loggedUsers) {
            sendNearEventsNotification(userId);
        }
    }

    /*
    * Descripció: Envia una notifiació a un usuari quan un dels seus esdeveniments s'aproxima
    */
    public void sendNearEventsNotification(String userId) {
        sendNotification(userId, "Esdeveniments propers", "Algun esdeveniment al que assisteixes s'aproxima! Comprova-ho a la app!");
    }
}
