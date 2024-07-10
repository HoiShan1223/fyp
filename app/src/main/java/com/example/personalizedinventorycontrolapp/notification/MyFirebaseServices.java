package com.example.personalizedinventorycontrolapp.notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.personalizedinventorycontrolapp.EditFamilyAccountLinkageActivity;
import com.example.personalizedinventorycontrolapp.MainActivity;
import com.example.personalizedinventorycontrolapp.NotificationController;
import com.example.personalizedinventorycontrolapp.worker.SendNotificationWorker;
import com.example.personalizedinventorycontrolapp.R;
import com.example.personalizedinventorycontrolapp.ViewShoppingListActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseServices extends FirebaseMessagingService {

    SharedPreferences sharedpreferences;
    private static final String TAG = "MyFirebaseServices_PushNotification";
    private static final String CHANNEL_ID = "1234";
    private static final String senderUseridFromNotification = "senderUseridFromNotification";


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getTitle());
        Log.d(TAG, "onMessageReceived: " + remoteMessage.getNotification().getBody());
        sharedpreferences = getSharedPreferences(MainActivity.MyPREFERENCES, Context.MODE_PRIVATE);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

                // Handle message within 10 seconds
                handleNow(Objects.requireNonNull(remoteMessage.getNotification().getTitle()), remoteMessage.getNotification().getBody(),remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            if(Objects.equals(remoteMessage.getNotification().getTitle(), "Unlink Family Account")
                    || Objects.equals(remoteMessage.getNotification().getTitle(), "Response to Family Account Linkage")
                    || Objects.equals(remoteMessage.getNotification().getTitle(), "Rejection on Family Account Linkage")){
                showLinkageResponseNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }else if(Objects.equals(remoteMessage.getNotification().getTitle(), "Shopping List")
                    || Objects.equals(remoteMessage.getNotification().getTitle(), "Alert on Early Purchasing")){
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
            }
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
        sendRegistrationToServer(token);
    }

    private void handleNow(String title, String message, Map<String, String> data) {

        if(title.equals("Link Family Account")){
            String userid = data.get("userid");
            String email = data.get("email");
            showLinkageRequestNotification(title, message, userid, email);
        }

        Log.d(TAG, "handleNow: " + senderUseridFromNotification);
        Log.d(TAG, "Short lived task is done.");

    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        Log.d(TAG, "onComplete: " + token);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(MainActivity.Firebase_Cloud_Messaging_Token, token);
        editor.apply();
        String shNewFCMToken = sharedpreferences.getString(MainActivity.Firebase_Cloud_Messaging_Token, "");
        Log.d(TAG, "shared preferences new token: " + shNewFCMToken);
    }
    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private void showNotification(String title, String message){
        Intent intent = null;
        PendingIntent pendingIntent = null;
        int shUserid = sharedpreferences.getInt(MainActivity.Userid, 0);
        if(shUserid != 0){
            if(title.equals("Shopping List") || title.equals("Alert on Early Purchasing")){
                intent = new Intent(this, ViewShoppingListActivity.class);
            }else if(title.equals("Rejection on Family Account Linkage") || title.equals(("Response to Family Account Linkage"))){
                intent = new Intent(this, EditFamilyAccountLinkageActivity.class);
            }
        }else{
            intent = new Intent(this, MainActivity.class);
        }
        if(intent != null){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        }


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }

    private void showLinkageRequestNotification(String title, String message, String userid,  String email){ // Notification with "YES" "NO" Action Button
        int user_id = sharedpreferences.getInt(MainActivity.Userid, 0);
        int SenderUserid = Integer.parseInt(userid);
        String shEmail = sharedpreferences.getString(MainActivity.Email, "");
        Intent yesIntent = new Intent(this, NotificationController.class);
        yesIntent.putExtra("TAPPED_YES", true);
        yesIntent.setAction("com.example.personalizedinventorycontrolapp.ACTION_YES");
        yesIntent.putExtra("ResponseUserid", user_id);
        yesIntent.putExtra("senderUserid", SenderUserid);
        yesIntent.putExtra("email", shEmail);
        yesIntent.putExtra("senderEmail", email);
        PendingIntent yesPendingIntent = PendingIntent.getBroadcast(this, 0, yesIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent noIntent = new Intent(this, NotificationController.class);
        noIntent.setAction("com.example.personalizedinventorycontrolapp.ACTION_NO");
        noIntent.putExtra("TAPPED_NO", true);
        noIntent.putExtra("ResponseUserid", user_id);
        noIntent.putExtra("senderUserid", SenderUserid);
        PendingIntent noPendingIntent = PendingIntent.getBroadcast(this, 0, noIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(yesPendingIntent)
                .setContentIntent(noPendingIntent)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_baseline_check_24, "Yes", yesPendingIntent)
                .addAction(R.drawable.ic_baseline_clear_24, "No", noPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());

    }

    private void showLinkageResponseNotification(String title, String message){
        Intent intent = new Intent(this, EditFamilyAccountLinkageActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, builder.build());
    }
}
