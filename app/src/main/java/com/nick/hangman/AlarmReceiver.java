package com.nick.hangman;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String NOTIFICATION_PREFS = "notificationPrefs";
    public static final String NOTIFICATION_KEY = "notificationKey";

    public static final String NOTIFICATION_ENABLE_PREFS = "notificationEnablePrefs";
    public static final String NOTIFICATION_ENABLE_KEY = "notificationEnableKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub

        long when = System.currentTimeMillis();
        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notificationIntent = new Intent(context, StartActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        int MID = 1;

        long fiveDays = TimeUnit.DAYS.toMillis(3);
        long nextNotification = getLastNotificationDay(context) + fiveDays;
        long now = System.currentTimeMillis();

        if(now >= nextNotification) {
            //Reset notification date
            setLastNotificationDay(context);

            if(isNotificationEnabled(context)) {

                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.image_icon_notification_72)
                        .setContentTitle("Hangman Tale")
                        .setContentText("Play to claim your daily hint coins").setSound(alarmSound)
                        .setAutoCancel(true).setWhen(when)
                        .setContentIntent(pendingIntent)
                        .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                notificationManager.notify(MID, mNotifyBuilder.build());
                MID++;

            }
        }

    }

    private long getLastNotificationDay(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE).edit();
        SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE);
        long restoredPref = prefs.getLong(NOTIFICATION_KEY, 0);
        if(restoredPref == 0) {
            restoredPref = System.currentTimeMillis();
            editor.putLong(NOTIFICATION_KEY, restoredPref);
            editor.commit();
        }
        return restoredPref;
    }

    private void setLastNotificationDay(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(NOTIFICATION_PREFS, Context.MODE_PRIVATE).edit();
        editor.putLong(NOTIFICATION_KEY, System.currentTimeMillis());
        editor.commit();
    }

    private boolean isNotificationEnabled(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(NOTIFICATION_ENABLE_PREFS, Context.MODE_PRIVATE);
        return prefs.getBoolean(NOTIFICATION_ENABLE_KEY, true);
    }

}