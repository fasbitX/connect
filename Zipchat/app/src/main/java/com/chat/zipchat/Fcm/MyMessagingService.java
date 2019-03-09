package com.chat.zipchat.Fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.chat.zipchat.Activity.MainActivity;
import com.chat.zipchat.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.chat.zipchat.Common.BaseClass.isAppOnForeground;
import static com.chat.zipchat.Common.BaseClass.myLog;

public class MyMessagingService extends FirebaseMessagingService {

    public static final String NOTIFICATION_CHANNEL_ID = "1001";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (!isAppOnForeground(this)) {

            if (remoteMessage.getNotification() != null) {
                sendNotification(remoteMessage.getNotification().getBody());
            }


            if (remoteMessage.getData().size() > 0) {
                try {
                    sendNotification(remoteMessage.getData().get("body"));
                } catch (Exception e) {
                    myLog("Exception: ", e.getMessage());
                }
            }

        }

    }

    private void sendNotification(String messageBody) {

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getResources().getString(R.string.app_name))
                        .setStyle(new Notification.BigTextStyle()
                                .bigText(messageBody))
                        .setVibrate(new long[]{100, 500})
                        .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                        .setAutoCancel(true)
                        .setContentText(messageBody);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert notificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        assert notificationManager != null;

        mBuilder.setContentIntent(pIntent);
        notificationManager.notify(0, mBuilder.build());


    }

}