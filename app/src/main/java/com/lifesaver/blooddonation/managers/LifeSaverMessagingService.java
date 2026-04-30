package com.lifesaver.blooddonation.managers;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.lifesaver.blooddonation.constants.AppConstants;

/**
 * Receives push notifications from Firebase Cloud Messaging
 * and surfaces them via NotificationHelper.
 */
public class LifeSaverMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage msg) {
        String title = msg.getNotification() != null
                ? msg.getNotification().getTitle()
                : msg.getData().getOrDefault("title", "LifeSaver");
        String body = msg.getNotification() != null
                ? msg.getNotification().getBody()
                : msg.getData().getOrDefault("body", "");
        NotificationHelper.show(this, (int) System.currentTimeMillis(), title, body);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Persist on the user document so the server can target this device.
        if (AuthManager.get().currentUser() != null) {
            String uid = AuthManager.get().currentUser().getUid();
            FirebaseFirestore.getInstance()
                    .collection(AppConstants.COLLECTION_USERS)
                    .document(uid)
                    .update("pushToken", token);
        }
    }
}
