package com.lksnext.parkingplantilla.domain;

import static android.app.Notification.FOREGROUND_SERVICE_IMMEDIATE;

import com.lksnext.parkingplantilla.R;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import android.os.Bundle;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class NotificationHelper {

    private static final String CHANNEL_ID = "reservas_channel";
    private static final int PERMISSION_REQUEST_CODE = 1001;

    /**
     * LLAMAR ESTO DESDE MainActivity.onCreate()
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal de Reservas";
            String description = "Notificaciones relacionadas con reservas";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /**
     * LLAMAR ESTO DESDE MainActivity.onCreate()
     */
    public static void requestNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                if (context instanceof android.app.Activity) {
                    ActivityCompat.requestPermissions((android.app.Activity) context,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            PERMISSION_REQUEST_CODE);
                }
            }
        }
    }

    /**
     * LLAMAR ESTO DESDE MainActivity.onRequestPermissionsResult()
     */
    public static void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.d("NOTIFICACION ENVIAR", "Permiso denegado para enviar notificaciones");
            }
        }
    }

    public static void scheduleNotification(Context context, String reservaId, String title, String text, long delayInMinutes) {
        Log.d("NOTIFICACION ENVIAR", "Programando notificación en " + delayInMinutes + " minutos");

        Data data = new Data.Builder()
                .putString("title", title)
                .putString("text", text)
                .putInt("id", reservaId.hashCode())
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInputData(data)
                .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                .addTag(reservaId)
                .build();

        WorkManager.getInstance(context).enqueueUniqueWork(
                reservaId,
                ExistingWorkPolicy.REPLACE,
                workRequest
        );
    }

    public static class NotificationWorker extends Worker {
        public NotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) {
            super(context, params);
        }

        @NonNull
        @Override
        public Result doWork() {
            String title = getInputData().getString("title");
            String text = getInputData().getString("text");
            int id = getInputData().getInt("id", 0);

            Log.d("NOTIFICACION ENVIAR", "Worker ejecutando notificación con título: " + title);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_parking)
                    .setContentTitle(title)
                    .setContentText(text)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setVibrate(new long[]{0, 500, 250, 500})
                    .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE)
                    .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                    ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS)
                            == PackageManager.PERMISSION_GRANTED) {
                NotificationManagerCompat.from(getApplicationContext()).notify(id, builder.build());
            } else {
            }
            return Result.success();
        }
    }
}



