package za.ac.uct.cs.videodatausageapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class CollectionService extends Service {

    private static final int NOTIFICATION_ID = 1234;
    private static final String CHANNEL_ID = "videoDataUsage_01";

    private final IBinder binder = new CollectionBinder();
    private NotificationManager notificationManager;

    public CollectionService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return this.binder;
    }

    public class CollectionBinder extends Binder {
        public CollectionService getService() {
            return CollectionService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Create notification channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        // Start in foreground
        startForeground(NOTIFICATION_ID, createServiceRunningNotification());
    }

    private Notification createServiceRunningNotification() {
        // The intent to launch when the user clicks the expanded notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendIntent =
                PendingIntent.getActivity(this, 0, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notice = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(getString(R.string.notification_service_started))
                .setWhen(System.currentTimeMillis())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendIntent)
                .build();
        notice.flags |=
                Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;

        return notice;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}