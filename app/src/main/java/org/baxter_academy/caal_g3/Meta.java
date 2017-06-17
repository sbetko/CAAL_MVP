package org.baxter_academy.caal_g3;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

/**
 * Created by Baxter on 1/13/2017.
 */

public class Meta extends Service {
    //1000ms * 10 = 10 second
    public int collectionInterval = 1*1; //Collection interval of 1ms
    //TODO define all file names for global reference
    public Intent readerIntent;
    public PendingIntent pintent;
    public AlarmManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        //for coreServiceChain
        readerIntent = new Intent(getApplicationContext(), Reader.class);
        pintent = PendingIntent.getService(this.getBaseContext(), 0, readerIntent, 0);
        manager = (AlarmManager) (this.getSystemService(Context.ALARM_SERVICE));

        // by starting reader service, the coreServiceChain is entered until user exits
        startService(readerIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(coreServiceChain,
                new IntentFilter("FinishedWork"));
        LocalBroadcastManager.getInstance(this).registerReceiver(errorHandler,
                new IntentFilter("Error"));
    }

    public BroadcastReceiver coreServiceChain = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String status = intent.getStringExtra("status");
            System.out.println(status + " " + context);
            if (status == "Reader finished") {
                Intent cleanerIntent = new Intent(context, Cleaner.class);
                startWakefulService(context, cleanerIntent);
            } else if (status == "Cleaner finished") {
                Intent wekaClassifierIntent = new Intent(context, WekaClassifier.class);
                startWakefulService(context, wekaClassifierIntent);
            } else if (status == "WekaClassifier finished") {
                Intent presentInterruptIntent = new Intent(context, PresentInterrupt.class);
                startWakefulService(context, presentInterruptIntent);
            } else if (status == "PresentInterrupt finished") {
                setAlarm();
        }
    }};

    public BroadcastReceiver errorHandler = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String solution = intent.getStringExtra("solution");
            System.out.println(solution + " " + context);
            if (solution == "Restart immediately") {
                Intent readerIntent = new Intent(Meta.this, Reader.class);
                startWakefulService(context, readerIntent);
            }
        }
    };

    public void setAlarm() {
            manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + collectionInterval, pintent);
    }

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
        LocalBroadcastManager.getInstance(this).unregisterReceiver(coreServiceChain);
    }
}