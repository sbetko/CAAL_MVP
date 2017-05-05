package org.baxter_academy.caal_g3;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Objects;

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
    WekaClassifier classifier;
    public BufferedWriter writer = null;
    public BufferedWriter sitWriter = null;
    public String activity = null;

    public long startSitTime;
    public long cTime;
    public long sitDuration;
    public long maxSitTime = 100;

    public void onCreate() {
        System.out.println("Started Present Interrupt");

        final WekaClassifier wekaClassifier = new WekaClassifier();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), wekaClassifier.arffDataLabeledFilename);

        /** loads activity from WekaClassifier file output **/
        try {

            BufferedReader reader = new BufferedReader(new FileReader(file)); //fixme reference to local declaration in WekaClassifier

            for(int i = 1; i < 51; i++) {
                reader.readLine();
            }

            String actLine = reader.readLine();
            String actLineSplit [] = actLine.split(",");

            /** If there's a classification, then proceed, otherwise restart (this is due to a BUG) **/
            if (Objects.equals(actLine, "") || (actLine.isEmpty())) {
                // make call to META that core service chain needs restarting
                Log.d("sender", "Broadcasting message");
                Intent intent = new Intent("Error");
                // You can also include some extra data.
                intent.putExtra("solution", "Restart immediately");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                stopSelf();
            }

            try {
                String activity = actLineSplit[45]; // pulls out specific name of activity
            } catch (ArrayIndexOutOfBoundsException e) {
                Intent intent = new Intent("Error");
                // You can also include some extra data.
                intent.putExtra("solution", "Restart immediately");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                stopSelf();
            }

            String activity = actLineSplit[45]; // pulls out specific name of activity

            reader.close();

            /** logs activity with timestamp **/
            //fixme CRITICAL logfile not written over on restart of coreServiceChain
            //fixme this means that on a resume of the service from the app GUI or after a crash,
            //fixme reminder logic will be broken

            //File logfile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), meta.activityLogFilename);
            // General activity logging
            writer = new BufferedWriter(new FileWriter(
                    new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog"), true)); //fixme hardcoded log file name
            writer.write(System.currentTimeMillis() + "," + activity);
            writer.newLine();
            writer.flush();
            writer.close();
            System.out.println(System.currentTimeMillis() + "," + activity); //debug

            /** Sitting-only logging (for interrupt logic, strictly for easier implementation) **/
            // creates new text file
            String actString = activity.toString(); //need to convert, or else .equals won't work
            String testString = "Sitting";
            System.out.println(actString + " = " + "Sitting");

            if (!actString.equals(testString)) {
                System.out.println(actString + " != " + "Sitting");
                new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "sittingLog"); //RESET LOG
            } else { //if (Objects.equals(activity, "Sitting")) {
                System.out.println(actString + " = " + "Sitting");

                sitWriter = new BufferedWriter(new FileWriter(
                        new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "sittingLog"), true
                ));
                sitWriter.write(System.currentTimeMillis() + "," + activity);
                sitWriter.newLine();
                sitWriter.flush();
                sitWriter.close();

                /** begins interrupt logic **/
                BufferedReader sitReader = new BufferedReader(
                        new FileReader(
                                new File(getExternalFilesDir(
                                        Environment.DIRECTORY_DOCUMENTS), "sittingLog")
                        )); //fixme hardcoded log file name

                int iter = 0;
                while (sitReader.readLine() != null) {
                    if (iter == 0) {
                        startSitTime = Long.parseLong(actLineSplit[1]);
                    }
                    if (iter > 0) {
                        sitDuration = System.currentTimeMillis() - startSitTime;
                    }
                    iter++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** Check if its time to remind the user **/
        if (sitDuration > maxSitTime) {
            //it's time, send notification
            NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                    //.setSmallIcon()
                    .setContentTitle("Take a break!")
                    .setContentTitle("It's time to stand");

            Intent resultIntent = new Intent(this, PresentInterrupt.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addParentStack(PresentInterrupt.class);
            stackBuilder.addNextIntent(resultIntent);
            PendingIntent resultPendingIntent =
                    stackBuilder.getPendingIntent(
                            0,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(1, mBuilder.build()); // int 1 is notification ID
        }
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
