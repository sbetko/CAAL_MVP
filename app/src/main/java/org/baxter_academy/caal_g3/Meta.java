package org.baxter_academy.caal_g3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;
import java.util.Objects;

/**
 * Created by Baxter on 1/13/2017.
 */

public class Meta extends Service {
    @Override
    //public int onStartCommand(Intent intent, int flags, int startId) {
    public void onCreate() {
        super.onCreate();
        System.out.println("started meta");
        // defines intents for making calls to services
        //stopService(readerIntent); //TODO somehow understand when Reader is finished (broadcast? binding?)
        Intent readerIntent = new Intent(this.getApplicationContext(), Reader.class);
        startService(readerIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("FinishedWork"));
        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        //stopSelf();
        //return START_STICKY;
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String status = intent.getStringExtra("status");
            System.out.println(status + " " + context);
            if (status == "Reader finished") {
                Intent cleanerIntent = new Intent(context, Cleaner.class);
                startService(cleanerIntent);
            } else if (status == "Cleaner finished") {
                Intent wekaClassifierIntent = new Intent(context, WekaClassifier.class);
                startService(wekaClassifierIntent);
            } else if (status == "WekaClassifier finished") {
                Intent presentInterruptIntent = new Intent(context, PresentInterrupt.class);
                startService(presentInterruptIntent);
            } else if (status == "PresentInterrupt finished") {
                try {
                    Thread.sleep(1000*10); //waits 10 seconds but locks UI thread?
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Intent readerIntent = new Intent(context, Reader.class);
                    startService(readerIntent);
                }
                Intent readerIntent = new Intent(context, Reader.class);
                startService(readerIntent);
            }

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("Stopped Meta");

        // stops reader in case it is running
        Intent readerIntent = new Intent(this.getApplicationContext(), Reader.class);
        stopService(readerIntent);

        // unregisters local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        /**
        // I want to restart this service again in 1 minute
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
                alarm.RTC_WAKEUP,
                System.currentTimeMillis() + (1000 * 60 * 1),
                PendingIntent.getService(this, 0, new Intent(this, Meta.class), 0)

        );
         **/
    }
}