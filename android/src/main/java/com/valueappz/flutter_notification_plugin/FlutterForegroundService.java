package com.valueappz.flutter_notification_plugin;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.core.app.NotificationCompat;


public class FlutterForegroundService extends Service {
    public static final String NOTIFICATION_CHANNEL_ID = "CHANNEL_ID";
    public static final String ACTION_STOP_SERVICE = "STOP";
    public static int ONGOING_NOTIFICATION_ID = 1;
    private static String TAG = "FlutterForegroundService";
    MediaPlayer mediaPlayer;
    Vibrator vibrator;
    private boolean userStopForegroundService = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.d(TAG, "onStartCommand: Intent is null");
            return START_NOT_STICKY;
        }
        if (intent.getAction() == null) {
            Log.d(TAG, "onStartCommand: Intent action is null");
            return START_NOT_STICKY;
        }
        final String action = intent.getAction();
        Log.d(TAG, String.format("onStartCommand: %s", action));
        Bundle bundle = intent.getExtras();
        switch (action) {
            case FlutterNotificationPlugin.START_FOREGROUND_ACTION:
                mediaPlayer = MediaPlayer.create(this, getSoundFile(bundle.getString("sound")));
                mediaPlayer.setLooping(true); // Set looping
                mediaPlayer.setVolume(10, 10);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        stopSelf();
                    }
                });

                mediaPlayer.start();
                vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 500 milliseconds
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createWaveform(new long[]{300, 300, 300, 1000}, VibrationEffect.EFFECT_DOUBLE_CLICK));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(500);
                }

                PackageManager pm = getApplicationContext().getPackageManager();
                Intent notificationIntent = pm.getLaunchIntentForPackage(getApplicationContext().getPackageName());
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                        notificationIntent, 0);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            "flutter_foreground_service_channel",
                            NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setSound(null, null);
                    channel.enableVibration(false);

                    ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                            .createNotificationChannel(channel);
                }
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(getNotificationIcon(bundle.getString("icon")))
                        .setColor(bundle.getInt("color"))
                        .setContentTitle(bundle.getString("title"))
                        .setContentText(bundle.getString("content"))
                        .setCategory(NotificationCompat.CATEGORY_SERVICE)
                        .setContentIntent(pendingIntent)
                        .setUsesChronometer(bundle.getBoolean("chronometer"))
                        .setOngoing(true);

                if (bundle.getBoolean("stop_action")) {
                    Intent stopSelf = new Intent(this, FlutterForegroundService.class);
                    stopSelf.setAction(ACTION_STOP_SERVICE);

                    PendingIntent pStopSelf = PendingIntent
                            .getService(this, 0, stopSelf, PendingIntent.FLAG_CANCEL_CURRENT);
                    builder.addAction(getNotificationIcon(bundle.getString("stop_icon")),
                            bundle.getString("stop_text"),
                            pStopSelf);
                }

                if (bundle.getString("subtext") != null && !bundle.getString("subtext").isEmpty()) {
                    builder.setSubText(bundle.getString("subtext"));
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
                    startForeground(ONGOING_NOTIFICATION_ID, builder.build());
                }
                break;
            case FlutterNotificationPlugin.STOP_FOREGROUND_ACTION:
                stopFlutterForegroundService();
                break;
            case ACTION_STOP_SERVICE:
                stopFlutterForegroundService();
                break;
            default:
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (!userStopForegroundService) {
            Log.d(TAG, "User close app, kill current process to avoid memory leak in other plugin.");
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            vibrator.cancel();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private int getNotificationIcon(String iconName) {
        int resourceId = getApplicationContext().getResources().getIdentifier(iconName, "drawable", getApplicationContext().getPackageName());
        return resourceId;
    }

    private int getSoundFile(String file) {
        int resourceId = getApplicationContext().getResources().getIdentifier(file, "raw", getApplicationContext().getPackageName());
        return resourceId;
    }

    private void stopFlutterForegroundService() {
        userStopForegroundService = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR) {
            stopForeground(true);
        }
        stopSelf();
    }
}
