package org.baxter_academy.caal_g3;

/**
 * Created by Baxter on 1/13/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;


public class Cleaner extends Service {
    // defines output file name
    public String arffUnlabeledFilename = "arffDataUnlabeled.arff";

    // gets input filename from Reader class
    Reader reader = new Reader();
    String rawDataFilename = reader.rawDataFilename;

    public void onCreate() {
        System.out.println("***********************STARTED CLEANER***********************");
        String rawDataFilePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + rawDataFilename; //todo declare path in Reader
        String arffDataFilePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS) + "/" + arffUnlabeledFilename; //cannot declare in outer scope because of illegal forward reference
        System.out.println("Started Cleaner");

        //defines arguments for StandAloneFeat (WISDM)
        String[] IOString = new String[] {rawDataFilePath, arffDataFilePath};
        //calls WISDM api
        StandAloneFeat.main(IOString);

        /** FINISH **/
        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Log.d("Cleaner", "Broadcasting message");
        Intent intent = new Intent("FinishedWork");
        // You can also include some extra data.
        intent.putExtra("status", "Cleaner finished");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

    }
}