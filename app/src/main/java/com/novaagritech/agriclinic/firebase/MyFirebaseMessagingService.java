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
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.novaagritech.agriclinic.R;
import com.novaagritech.agriclinic.activities.SingleArticleActivity;
import com.novaagritech.agriclinic.activities.SingleEventActivity;
import com.novaagritech.agriclinic.activities.SingleNewsActivity;
import com.novaagritech.agriclinic.activities.SingleSchemesActivity;

import org.json.JSONObject;

import java.util.Map;

import static com.google.firebase.remoteconfig.FirebaseRemoteConfig.TAG;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        Log.e(TAG, "From: " + message.getData());
        FirebaseMessaging.getInstance().setAutoInitEnabled(true);

        Map<String,String> data=message.getData();
        JSONObject dataObject=new JSONObject(data);
        String notification_data=dataObject.optString("message");
        Log.e(TAG, "From: " + notification_data);
        handleDataMessage(dataObject);


    }

    @Override
    public void onNewToken(@NonNull String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer( String token) {
        // TODO: Implement this method to send token to your app server.
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("TOKEN1", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        // Log and toast
                        String msg = getString(R.string.fcm_token, token);
                        Log.d("TOKEN1", msg);
                    }
                });
    }
   /* private void sendNotification(String notification_data) {
        Intent intent = new Intent(this, HomeActivity.class);
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
        String title = null;
        String category = null;
        String id = null;

        try {
            message = json.getString("message");
            title = json.getString("title");
            category = json.getString("category");
            id = json.getString("id");
            ///  boolean isBackground = data.getBoolean("is_background");
            // String imageUrl = data.getString("image");
            // String timestamp = data.getString("timestamp");
            // JSONObject payload = data.getJSONObject("payload");

            //id=23, category=news

            // Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "category: " + category);
           /* Log.e(TAG, "isBackground: " + isBackground);
            Log.e(TAG, "payload: " + payload.toString());
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);*/
            if (category.equalsIgnoreCase("news")) {

                Intent intent = new Intent(this, SingleNewsActivity.class);
                intent.putExtra("news_id",id);


                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                //contentView.setImageViewResource(R.id.image, R.mipmap.logo);


                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(json.getString("image"))
                        .submit(512, 512)
                        .get();

                contentView.setImageViewBitmap(R.id.image, bitmap);


                contentView.setTextViewText(R.id.textTitle, json.getString("title"));
                contentView.setTextViewText(R.id.textDesc, json.getString("message"));

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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

            }
            if (category.equalsIgnoreCase("article")) {

                Intent intent = new Intent(this, SingleArticleActivity.class);
                intent.putExtra("article_id",id);


                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                //contentView.setImageViewResource(R.id.image, R.mipmap.logo);


                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(json.getString("image"))
                        .submit(512, 512)
                        .get();

                contentView.setImageViewBitmap(R.id.image, bitmap);


                contentView.setTextViewText(R.id.textTitle, json.getString("title"));
                contentView.setTextViewText(R.id.textDesc, json.getString("message"));

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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

            }

            if (category.equalsIgnoreCase("govt_schemes")) {

                Intent intent = new Intent(this, SingleSchemesActivity.class);
                intent.putExtra("scheme_id",id);


                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                //contentView.setImageViewResource(R.id.image, R.mipmap.logo);


                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(json.getString("image"))
                        .submit(512, 512)
                        .get();

                contentView.setImageViewBitmap(R.id.image, bitmap);


                contentView.setTextViewText(R.id.textTitle, json.getString("title"));
                contentView.setTextViewText(R.id.textDesc, json.getString("message"));

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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

            }

            if (category.equalsIgnoreCase("events")) {

                Intent intent = new Intent(this, SingleEventActivity.class);
                intent.putExtra("event_id",id);


                RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification_layout);
                //contentView.setImageViewResource(R.id.image, R.mipmap.logo);


                Bitmap bitmap = Glide.with(this)
                        .asBitmap()
                        .load(json.getString("image"))
                        .submit(512, 512)
                        .get();

                contentView.setImageViewBitmap(R.id.image, bitmap);


                contentView.setTextViewText(R.id.textTitle, json.getString("title"));
                contentView.setTextViewText(R.id.textDesc, json.getString("message"));

                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder notificationbuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContent(contentView)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
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

            }
            } catch(Exception e){
                e.printStackTrace();
            }




    }
}
