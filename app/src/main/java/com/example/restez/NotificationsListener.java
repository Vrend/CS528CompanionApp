package com.example.restez;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.restez.Model.AppModel;
import com.example.restez.Utils.DatabaseHandler;

import java.util.Set;

public class NotificationsListener extends NotificationListenerService {


    private DatabaseHandler db;


    @Override
    public void onCreate() {
        super.onCreate();
    }


    @Override
    public void onListenerConnected() {
        System.out.println("NOTIF LISTENER ENABLED");
    }

    @Override
    public IBinder onBind(Intent intent) {
        System.out.println("BOUND NOTIFICATION LISTENER!");
        return super.onBind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn){
        db = new DatabaseHandler(getApplicationContext());
        db.openDatabase();

        String packageName = sbn.getPackageName();

        for (AppModel app : db.getAllApps()) {
            if (app.getPackageName().equals(packageName)) {
                // app matches something in the database
                int selectionType = app.getType();

                if (selectionType == 0) {
                    // code for sending signal with if statement here
                    System.out.println("Alert received!");
                    Intent i = new Intent("com.example.restez.NOTIFICATION");
                    sendBroadcast(i);
                }

                else if (selectionType == 1) {
                    // isExactly
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (sbn.getNotification().extras.get("android.title").toString().equals(app.getSearch())) {
                            // alert stuff
                            System.out.println("Alert received!");
                            Intent i = new Intent("com.example.restez.NOTIFICATION");
                            sendBroadcast(i);
                        }
                    }
                }
                else {
                    // contains
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String title = sbn.getNotification().extras.get("android.title").toString();
                        if (title.contains(app.getSearch())) {
                            // alert stuff
                            Intent i = new Intent("com.example.restez.NotificationsListener");
                            sendBroadcast(i);
                        }
                    }
                }
            }



        }






        // Implement what you want here
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn){
        // Implement what you want here
    }
}
