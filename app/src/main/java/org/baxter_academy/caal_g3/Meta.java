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

/**
 * Created by Baxter on 1/13/2017.
 */

public class Meta extends Service {

    @Override
    public void onCreate() {
        super.onCreate();

        //for coreServiceChain
        Intent readerIntent = new Intent(this.getApplicationContext(), Reader.class);
        startService(readerIntent);
        LocalBroadcastManager.getInstance(this).registerReceiver(coreServiceChain,
                new IntentFilter("FinishedWork"));
    }

    public BroadcastReceiver coreServiceChain = new BroadcastReceiver() {
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
                setAlarm();
            }
        }
    };

    public void setAlarm() {
        Intent readerIntent = new Intent(Meta.this, Reader.class);
        PendingIntent pintent = PendingIntent.getService(this.getBaseContext(), 0, readerIntent, 0 ); //calling this.getApplicationContext = nullpointer
        AlarmManager manager = (AlarmManager)(this.getSystemService(Context.ALARM_SERVICE ));
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000 * 10, pintent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        System.out.println("Stopped Meta");

        // stops reader in case it is running
        //TODO does Reader need to scrub the rawData file?
        Intent readerIntent = new Intent(this.getApplicationContext(), Reader.class);
        stopService(readerIntent);

        // unregisters local broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(coreServiceChain);
    }
}