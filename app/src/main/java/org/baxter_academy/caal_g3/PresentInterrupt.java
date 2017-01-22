package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by lyana on 1/18/2017.
 */

public class PresentInterrupt extends Service {

    public void onCreate() {
        System.out.println("Started PresentInterrupt");
        // load logs from WekaClassifer (csv format)
        // check if its time to remind the user
        stopSelf();
    }

    public void onDestroy() {
        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("FinishedWork");
        // You can also include some extra data.
        intent.putExtra("status", "PresentInterrupt finished");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
