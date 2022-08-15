package com.blooddonation;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class App extends Application {
    public static final String CHANNEL_ID = "example_service_channel";

    @Override
    public void onCreate() {
        super.onCreate();
        create_notification_channel();
    }

    private void create_notification_channel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannelService = new NotificationChannel(
                    CHANNEL_ID,
                    "Example service",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = (NotificationManager) getSystemService( NOTIFICATION_SERVICE );
            manager.createNotificationChannel( notificationChannelService );
        }
    }
}
