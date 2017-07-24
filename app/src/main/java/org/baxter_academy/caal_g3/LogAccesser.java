package org.baxter_academy.caal_g3;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by lyana on 21/07/2017.
 */

public class LogAccesser extends ContextWrapper {

    public LogAccesser(Context base) {
        super(base);
    }

    /**
     * Returns the most recent date-activity pair from the external storage CSV database
     * @return String [Uptime (ms), Date, Activity]
     */

    public String [] getMostRecent() {

        String line = new String();
        String [] lineSplit = new String[2];

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog")
            ));

            while (br.readLine() != null) {
                line = br.readLine();
            }

            if (line != null) {
                lineSplit = line.split(",");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        if (lineSplit.length == 3) {
            return lineSplit;
        } else {
            return null;
        }
    }

     //TODO Research methods of storing large sets of personal data in Android

    /**
     * Returns a HashMap of all date-activity pairs in the CSV file dataset.
     * May be a very slow operation depending on size of dataset.
     * @return HashMap of all date-activity pairs
     *
     */

    public HashMap<String, String> getAll() {

        String line;
        String [] lineSplit;
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog")
            ));

            while (br.readLine() != null) {
                line = br.readLine();
                lineSplit = line.split(",");

                hashMap.put(lineSplit[1], lineSplit[2]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return hashMap;
    }
}