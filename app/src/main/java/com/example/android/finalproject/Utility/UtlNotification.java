package com.example.android.finalproject.Utility;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

public class UtlNotification {

    private Context context;
    private static int notificationCounter = 0;
    private int notificationID;
    private int icon ;
    private NotificationManager notificationManager;
    private NotificationCompat.Builder builder;
    private CharSequence title;
    private String text;
    private Intent intent;
    private PendingIntent resultPendingIntent;

    public UtlNotification(){}

    public UtlNotification(int icon, CharSequence title, String text, Intent intent, Context context)
    {
        notificationID=++notificationCounter;
        this.icon=icon;
        this.title=title;
        this.text=text;
        this.intent=intent;
        this.context=context;

        resultPendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public  void sendNotification()
    {
        builder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(001,builder.build());
    }
}