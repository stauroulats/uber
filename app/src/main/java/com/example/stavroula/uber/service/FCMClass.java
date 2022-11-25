package com.example.stavroula.uber.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.RequestCallActivity;
import com.example.stavroula.uber.RiderMapActivity;
import com.example.stavroula.uber.WelcomeDriverActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

public class FCMClass extends FirebaseMessagingService {

    private final String TAG = "JSA-FCM";
    private final String TOPIC = "/topics/tripRequest";
    private final String TRIPTOPIC = "/topics/trip";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

         if (remoteMessage.getFrom().equals(TOPIC)) {
             if (remoteMessage.getNotification() != null) {
                 Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
                 Log.e(TAG, "Body: " + remoteMessage.getNotification().getBody());

                 Log.wtf("123", "response" + new Gson().toJson(remoteMessage.getNotification().getBody().toString()));

                 Log.wtf(TAG, "TOPIC: " + remoteMessage.getFrom());
                 remoteMessage.getNotification().getBody();
             }
             Log.wtf(TAG, "TOPIC: " + remoteMessage.getTo() + remoteMessage.getMessageType()+ remoteMessage.getCollapseKey());

             if (remoteMessage.getData().size() > 0) {
                 Log.e(TAG, "Data: " + remoteMessage.getData());
                 sendNotification(remoteMessage);
                 Intent intent = new Intent(this, RequestCallActivity.class);
                 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                 intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                 intent.putExtra("body", remoteMessage.getNotification().getBody());
                 intent.putExtra("title", remoteMessage.getNotification().getTitle());
                 intent.putExtra("data1", remoteMessage.getData().get("trip-request_id"));
                 intent.putExtra("data2", remoteMessage.getData().get("rider_id"));
                 startActivity(intent);
             }
         }
        if (remoteMessage.getFrom().equals(TRIPTOPIC)) {

            if (remoteMessage.getNotification() != null) {
                Log.e(TAG, "Title: " + remoteMessage.getNotification().getTitle());
                Log.e(TAG, "Body: " + remoteMessage.getNotification().getBody());

                Log.wtf("123", "response" + new Gson().toJson(remoteMessage.getNotification().getBody().toString()));
                remoteMessage.getNotification().getBody();
            }
            if (remoteMessage.getData().size() > 0) {
               String title = remoteMessage.getNotification().getTitle();
                Log.wtf("123", "title" + title);

                String tag = remoteMessage.getData().get("tag");
                Log.wtf("123", "TAG" + tag);
                if (tag.equals("driverArrival")) {
                    sendDriverArrivalNotification(remoteMessage);
                    Intent broadcastIntent = new Intent("driver-arrival");
                    broadcastIntent.putExtra("data1", remoteMessage.getNotification().getBody());
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                } else if (tag.equals("driverData")) {
                    Log.e(TAG, "Data: " + remoteMessage.getData());
                    sendDriverInfoNotification(remoteMessage);
                    Intent broadcastIntent = new Intent("acceptTripRequestNotification");
                    Log.wtf(TAG, "Data:TripId " + remoteMessage.getData().get("tripRequestId"));
                    broadcastIntent.putExtra("data1", remoteMessage.getData().get("tripRequestId"));
                    broadcastIntent.putExtra("data2", remoteMessage.getData().get("firstName"));
                    broadcastIntent.putExtra("data3", remoteMessage.getData().get("lastName"));
                    broadcastIntent.putExtra("data4", remoteMessage.getData().get("manufacturer"));
                    broadcastIntent.putExtra("data5", remoteMessage.getData().get("model"));
                    broadcastIntent.putExtra("data6", remoteMessage.getData().get("registrationPlate"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                    //startActivity(intent);
                } else if (tag.equals("driver-cancelRequest")){
                    Log.e(TAG, "Data: " + remoteMessage.getData());
                    sendDriverCancelNotification(remoteMessage);
                    Intent broadcastIntent = new Intent("cancelTripRequestNotification");
                    broadcastIntent.putExtra("data1", remoteMessage.getData().get("tripRequestId"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                }
                else if (tag.equals("rider-cancelRequest")){
                    Log.e(TAG, "Data: " + remoteMessage.getData());
                    sendRiderCancelNotification(remoteMessage);
                    Intent broadcastIntent = new Intent("cancelTripNotification");
                    broadcastIntent.putExtra("data1", remoteMessage.getData().get("tripRequestId"));
                    LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                }
                else if (tag.equals("payment")){
                    sendPaymentfoNotification(remoteMessage);
                    //Intent broadcastIntent = new Intent("creditCard-payment");
                    //broadcastIntent.putExtra("data1", remoteMessage.getData().get("tripRequestId"));
                    //LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
                }

            }
        }
    }


    private void sendNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Intent intent = new Intent(this, RequestCallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("data1", remoteMessage.getData().get("trip-request_id"));
        intent.putExtra("data2", remoteMessage.getData().get("rider_id"));

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }


    private void sendDriverInfoNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, RiderMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(324, notificationBuilder.build());
    }


    private void sendDriverArrivalNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, RiderMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(324, notificationBuilder.build());
    }
    private void sendDriverCancelNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, RiderMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(324, notificationBuilder.build());
    }

    private void sendRiderCancelNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, WelcomeDriverActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(324, notificationBuilder.build());
    }

    private void sendPaymentfoNotification(RemoteMessage remoteMessage) {
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(this,RiderMapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);


        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_car)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setPriority(Notification.PRIORITY_MAX)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        notificationManager.notify(324, notificationBuilder.build());
    }

}


