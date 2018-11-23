package org.airbloc.airbridgecustomtest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.i(Config.TAG, "onMessageReceived");

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String messagae = data.get("content");
        String airbridgeLink = data.get("redirectLink");

        Log.i(Config.TAG, "get Title : " + title);
        Log.i(Config.TAG, "get Content : " + messagae);
        Log.i(Config.TAG, "get RedirectLink : " + airbridgeLink);

        sendNotification(title, messagae, airbridgeLink);
    }


    private void sendNotification(String title, String message, String airbridgeLink) {
        Intent intent = new Intent(this, HomeActivtiy.class);
        intent.putExtra("airbridgeLink", airbridgeLink);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_dialog_info))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


            NotificationChannel notificationChannel = new NotificationChannel("CustomTester", "CustomTester Notification Channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("This Channel is engaged with CustomTester");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.GREEN);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 100, 200});
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            try{
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
                notificationBuilder.setChannelId("CustomTester");
            }catch (Exception e){
                Log.d(Config.TAG,e.getMessage());
            }

        }


        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());

    }
}
