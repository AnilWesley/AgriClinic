package com.novaagritech.agriclinic.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.HomeActivity1;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG, "From: " + message.getData());

        Map<String,String> data=message.getData();
        JSONObject dataObject=new JSONObject(data);
        String notification_data=dataObject.optString("message");
        Log.e(TAG, "From: " + notification_data);
        handleDataMessage(dataObject);


    }

   /* private void sendNotification(String notification_data) {
        Intent intent = new Intent(this, HomeActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle(notification_data)
                .setContentText(notification_data)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(new NotificationCompat.BigTextStyle())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }*/




    private void handleDataMessage(JSONObject json) {
        Log.e(TAG, "push json: " + json.toString());

            // JSONObject data = json.getJSONObject("data");

            //String title = data.getString("title");
        String message = null;
        try {
            message = json.getString("message");
            ///  boolean isBackground = data.getBoolean("is_background");
            // String imageUrl = data.getString("image");
            // String timestamp = data.getString("timestamp");
            // JSONObject payload = data.getJSONObject("payload");

            // Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
           /* Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/
            Intent intent = new Intent(this, HomeActivity1.class);


            RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
            //contentView.setImageViewResource(R.id.image, R.mipmap.logo);


            try {
                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(json.getString("image"))
                        .submit(512, 512)
                        .get();

                contentView.setImageViewBitmap(R.id.image, bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }


            contentView.setTextViewText(R.id.textTitle, json.getString("title"));
            contentView.setTextViewText(R.id.textDesc, json.getString("message"));

            Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder notificationbuilder = new Notification.Builder(this)
                    .setSmallIcon(R.mipmap.logo)
                    .setContent(contentView)
                    .setAutoCancel(true)
                    .setSound(defaultSoundUri)
                    .setFullScreenIntent(pendingIntent, true)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent);
            Notification notification = notificationbuilder.build();
            notification.flags |= Notification.FLAG_AUTO_CANCEL;
            notification.defaults |= Notification.DEFAULT_SOUND;
            notification.defaults |= Notification.DEFAULT_VIBRATE;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channelId = "channel_id";
                NotificationChannel channel = new NotificationChannel(channelId, json.getString("message"), NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(json.getString("message"));
                notificationManager.createNotificationChannel(channel);
                notificationbuilder.setChannelId(channelId);
            }
            notificationManager.notify(0, notificationbuilder.build());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


}
