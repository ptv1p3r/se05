package pt.ismat.se05;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

//import androidx.annotation.Nullable;
//import androidx.core.app.NotificationCompat;

public class MyService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            Notification.Builder builder = new Notification.Builder(this,"messages")
                    .setContentText("This is running in Background")
                    .setContentTitle("GPS Data tracking")
                    .setSmallIcon(R.drawable.ic_android_black_24dp);

            startForeground(101,builder.build());
        }

    }

//    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
