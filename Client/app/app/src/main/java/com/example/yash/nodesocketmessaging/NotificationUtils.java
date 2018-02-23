package com.example.yash.nodesocketmessaging;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yash on 23-02-2018.
 */

public class NotificationUtils {

    private Map<Integer, String> messageType;

    public NotificationUtils() {
        messageType = new HashMap<>();
        messageType.put(1, "Wrote A New Post");
        messageType.put(2, "Commented");
        messageType.put(3, "Reacted");
        messageType.put(4, "Wrote A Slam For You");
        messageType.put(5, "Sent You A Friend Request");
        messageType.put(6, "Started Following You");
        messageType.put(7, "message You");

    }

    //messageType:- Comment,Wrote A Post, messaged you...
    //content :- content to show
    public void showMessageNotification(Context context, int messageType, String content, String doer) {

        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
        inboxStyle.addLine(content);


        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(context);
        builder.setTicker("Klydo")
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle("Klydo")
                .setContentIntent(contentIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_LIGHTS)
                .setVibrate(new long[]{200, 300, 200, 300})
                .setStyle(inboxStyle)
                .setPriority(Notification.PRIORITY_MAX)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(doer + " " + this.messageType.get(messageType));

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(101, builder.build());
    }

}
