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
import java.io.PrintWriter;
import java.util.Objects;

/**
 * Created by lyana on 1/18/2017.
 */

public class PresentInterrupt extends Service {

    Meta meta;
    WekaClassifier classifier;
    public BufferedWriter writer = null;
    public BufferedWriter sitWriter = null;
    public String activity = null;
    public boolean noActivity = false;

    public long startSitTime;
    public long cTime;
    public long sitDuration;
    public long maxSitTime = 10000;

    public void onCreate() {
        System.out.println("Started Present Interrupt");

        final WekaClassifier wekaClassifier = new WekaClassifier();

        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), wekaClassifier.arffDataLabeledFilename);

        /** loads activity from WekaClassifier file output **/
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file)); //fixme reference to local declaration in WekaClassifier

            for (int i = 1; i < 51; i++) {
                reader.readLine();
            }

            String actLine = reader.readLine();
            String actLineSplit[] = actLine.split(",");

            /** If there's a classification, then proceed, otherwise restart (this is due to a BUG) **/
            if (!Objects.equals(actLine, "") || !(actLine.isEmpty())) {
                noActivity = false;
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

                if (!actString.equals(testString)) {
                    System.out.println(actString + " != " + "Sitting");
                    new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "sittingLog"); //RESET LOG
                } else {
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

                /** Check if its time to remind the user **/
                if (sitDuration > maxSitTime) {
                    //it's time, send notification
                    System.out.println("Sent Notification");
                    NotificationCompat.Builder mBuilder =
                            (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.startbutton)
                                    .setContentTitle("Take a break!")
                                    .setContentText("It's time to get moving!")
                                    .setPriority(2); //PRIORITY_MAX
                    // Creates an explicit intent for an Activity in your app
                    Intent resultIntent = new Intent(this, MainActivity.class);

                    // The stack builder object will contain an artificial back stack for the
                    // started Activity.
                    // This ensures that navigating backward from the Activity leads out of
                    // your application to the Home screen.
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                    // Adds the back stack for the Intent (but not the Intent itself)
                    stackBuilder.addParentStack(MainActivity.class);
                    // Adds the Intent that starts the Activity to the top of the stack
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    // mId allows you to update the notification later on.
                    mNotificationManager.notify(1, mBuilder.build());
                    //also, write over sittingLog TODO verify that this works
                    new PrintWriter(
                            new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "sittingLog")
                    );
                }
                stopSelf();
            } else {
                // make call to META that core service chain needs restarting
                noActivity = true;
                System.out.println("Restarted due to missing classification");
                Intent intent = new Intent("Error");
                intent.putExtra("solution", "Restart immediately");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                stopSelf();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        if (!noActivity) {
            Log.d("sender", "Broadcasting message");
            Intent intent = new Intent("FinishedWork");
            // You can also include some extra data.
            intent.putExtra("status", "PresentInterrupt finished");
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
