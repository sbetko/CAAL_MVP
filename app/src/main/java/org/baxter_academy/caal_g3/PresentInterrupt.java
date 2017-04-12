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

            // pulls out specific name of activity
            String lastLine = reader.readLine();
            String activityLine [] = lastLine.split(",");
            String activity = activityLine[45];

            // logs this name (the activity) with t5mestamp
            //TODO add logging of activity
            //File logfile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), meta.activityLogFilename);

            try {

                writer = new BufferedWriter(new FileWriter(
                        new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog"), true)); //fixme hardcoded log file name

                writer.write(System.currentTimeMillis() + "," + activity); //fixme logfile not written over on restart of coreServiceChain
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
