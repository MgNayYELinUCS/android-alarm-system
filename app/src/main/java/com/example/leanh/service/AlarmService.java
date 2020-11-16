package com.example.leanh.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.example.leanh.activity.AddAlarmActivity;
import com.example.leanh.activity.AlarmMainActivity;
import com.example.leanh.activity.R;
import com.example.leanh.receiver.AlarmReceiver;
import com.example.leanh.ultil.Constants;

public class AlarmService extends Service {
    MediaPlayer mediaPlayer; // this object to manage media

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO: processing on and off ringtone
        // get string from intent
        String on_Off = intent.getExtras().getString("ON_OFF");
        switch (on_Off) {
            case Constants.ADD_INTENT: // if string like this set start media
                // this is system default alarm alert uri
                Uri uri = Settings.System.DEFAULT_ALARM_ALERT_URI;
                // create mediaPlayer object
                mediaPlayer = MediaPlayer.create(this, uri);
                mediaPlayer.isLooping();
                mediaPlayer.start();
                addNotification(getApplicationContext());
                 break;
            case Constants.OFF_INTENT:
                // this check if user pressed cancel
                // get the alarm cancel id to check if equal the
                // pendingIntent'trigger id(pendingIntent request code)
                // the AlarmReceiver.pendingIntentId is taken from AlarmReceiver
                // when one pendingIntent trigger
                int alarmId = intent.getExtras().getInt("AlarmId");
                // check if mediaPlayer created or not and if media is playing and id of
                // alarm and trigger pendingIntent is same  then stop music and reset it
                if (mediaPlayer != null && mediaPlayer.isPlaying() && alarmId == AlarmReceiver.pendingId) {
                    // stop media
                    mediaPlayer.stop();
                    // reset it
                    mediaPlayer.reset();
                }
                break;


        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //TODO: Xử lý logic tắt nhạc chuông
        mediaPlayer.stop();
        mediaPlayer.reset();
    }
    public void addNotification(Context context) {
        NotificationManager mNotificationManager;
        MediaPlayer mediaPlayer =  MediaPlayer.create(getApplicationContext(), R.raw.iphone_ringtone);
        mediaPlayer.start();

        //Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Uri sound = Uri. parse (ContentResolver. SCHEME_ANDROID_RESOURCE + "://" + getPackageName()+ "/"  + R.raw.iphone_ringtone ) ;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext().getApplicationContext(), "notify_001");
        Intent ii = new Intent(getApplicationContext().getApplicationContext(), AlarmMainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("You reached your goal for today's steps.Good jobs.");
        bigText.setBigContentTitle("Today's Goal Reached.");
        bigText.setSummaryText("Good Jobs.");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setLargeIcon(BitmapFactory.decodeResource (getResources() , R.drawable.ic_add));
        mBuilder.setSmallIcon(R.mipmap.ic_launcher_round);
        mBuilder.setContentTitle("Today's Goal Reached.");
        mBuilder.setContentText("Today's Goal Reached.");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);
        mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        mBuilder.setSound(sound);
        mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.enableVibration(true);
            channel.setSound(sound,audioAttributes);

            if(mNotificationManager !=null){
                mNotificationManager.createNotificationChannel(channel);
            }
            mBuilder.setChannelId(channelId);
            mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

            mBuilder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
            mBuilder.setSound(sound);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }


    public IBinder onBind(Intent intent) {
        return null;
    }
}