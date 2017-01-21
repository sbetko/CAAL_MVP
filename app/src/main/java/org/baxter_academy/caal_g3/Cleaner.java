package org.baxter_academy.caal_g3;

/**
 * Created by Baxter on 1/13/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//TODO write pre-processing / ARFF formatting functions
/** Cleaner pre-processes data before it is classified **/
public class Cleaner extends Service {
    public void onCreate() {

        System.out.println("Started Cleaner");
        String arffDataFilename = "unlabeledData";
        BufferedWriter arffDataWriter = null;

        try {
            arffDataWriter = new BufferedWriter(
                    new FileWriter(new File(getFilesDir(), arffDataFilename)
            ));
        } catch (IOException e) {
            e.printStackTrace();
        }


        StringBuilder toWrite = new StringBuilder();

        toWrite.append("@relation activity_recognition_labeled");
        toWrite.append("'@attribute \"X8\" numeric");
        toWrite.append("@attribute \"Z3\" numeric");
        toWrite.append("@attribute \"ZAVG\" numeric");
        toWrite.append("@attribute \"XPEAK\" numeric");
        toWrite.append("@attribute \"ZPEAK\" numeric");
        toWrite.append("@attribute \"YSTANDDEV\" numeric");
        toWrite.append("@attribute class{ \"Sitting\" , \"Active\" }");
        toWrite.append("@data");
        toWrite.append("0.13,0.12,1.76,2075,1550,8.17,?");
        toWrite.append("0,0,8.24,257.35,315,0.03,?");

        String finalString = toWrite.toString();

        try {
            System.out.println(finalString); // debug
            if (arffDataWriter != null) {
                arffDataWriter.write(finalString);
                arffDataWriter.newLine();
                arffDataWriter.flush();
                arffDataWriter.close();
                System.out.println("file reference is not null");
            } else {
                System.out.println("file reference is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /** FINISH **/
        stopSelf();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Intent wekaClassifierIntent = new Intent(this.getBaseContext(), WekaClassifier.class);
        startService(wekaClassifierIntent);

    }
}