package com.example.restez;

import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.example.restez.Model.AppModel;
import com.example.restez.Utils.DatabaseHandler;

public class NotificationsListener extends NotificationListenerService {


    private DatabaseHandler db;

    @Override
    public IBinder onBind(Intent intent) {
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
                }

                else if (selectionType == 1) {
                    // isExactly
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (sbn.getNotification().getSettingsText().toString().equals(app.getSearch())) {
                            // alert stuff
                        }
                    }
                }
                else {
                    // contains
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (sbn.getNotification().getSettingsText().toString().contains(app.getSearch())) {
                            // alert stuff
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
