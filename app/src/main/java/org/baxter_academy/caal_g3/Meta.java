package org.baxter_academy.caal_g3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import java.util.List;

/**
 * Created by Baxter on 1/13/2017.
 */

public class Meta extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent readerIntent = new Intent(this.getApplicationContext(), Reader.class);
        Intent cleanerIntent = new Intent(this.getApplicationContext(), Cleaner.class);

        startService(readerIntent);
        try {
            Thread.sleep(1000*10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        stopService(readerIntent);
        startService(cleanerIntent);

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 1),
                PendingIntent.getService(this, 0, new Intent(this, Meta.class), 0)
        );
    }
}