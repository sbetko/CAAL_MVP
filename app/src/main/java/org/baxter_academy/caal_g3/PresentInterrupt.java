package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by lyana on 1/18/2017.
 */

/**
 * Possible use cases
 * Active -> Active
 * Active -> Sitting
 * Sitting -> Sitting
 * Sitting -> Active
 * Init. (null) -> Active
 * Init. (null) -> Sitting
 *
 * baseline logic
 * If init. & cAct = sitting
 *      startSitTime = cTime
 * If lastAct = active & cAct = sitting
 *      startSitTime = cTime
 * if lastAct = sitting
 *      if cAct = sitting
 *          sitDuration = cTime - startSitTime
 *      if cAct = active
 *          sitDuration = 0
 *
 * if sitDuration > durationLimit
 *      send push notification
 */

public class PresentInterrupt extends Service {

    Meta meta;
    public BufferedWriter writer = null;

    public void onCreate() {
        System.out.println("Started Present Interrupt");

        final WekaClassifier wekaClassifier = new WekaClassifier();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), wekaClassifier.arffDataLabeledFilename);

        // loads activity from WekaClassifier

        try {

            BufferedReader reader = new BufferedReader(new FileReader(file)); //fixme reference to local declaration in WekaClassifier

            for(int i = 1; i < 51; i++) {
                reader.readLine();
            }

            String lastLine = reader.readLine();
            String activityLine [] = lastLine.split(",");
            String activity = activityLine[45]; // pulls out specific name of activity

            // logs this name (the activity) with timestamp

            //File logfile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), meta.activityLogFilename);

            try {

                writer = new BufferedWriter(new FileWriter(
                        new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog"), true)); //fixme hardcoded log file name

                //fixme CRITICAL logfile not written over on restart of coreServiceChain
                //this means that on a resume of the service from the app GUI or after a crash,
                //reminder logic will be broken
                writer.write(System.currentTimeMillis() + "," + activity);
                writer.newLine();
                writer.flush();
                System.out.println(System.currentTimeMillis() + "," + activity);

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // check if its time to remind the user
        //TODO add timing logic
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
