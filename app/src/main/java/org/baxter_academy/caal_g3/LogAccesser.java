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
import android.text.style.TtsSpan;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

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

    /**
     * Parses through entire CSV database to populate an ArrayList with today's entries.
     * @return Two layer nested ArrayList formatted as [[Date, Activity], ...]
     */

    public ArrayList<ArrayList> getToday() throws ArrayIndexOutOfBoundsException {

        String line;
        String [] lineSplit;
        String cIterDateString;
        String cIterActivityString;
        Calendar cIterCal = Calendar.getInstance();
        ArrayList<ArrayList> todayArray = new ArrayList<>();
       // ArrayList<Object> cIterRow = new ArrayList<>();
        ArrayList<Calendar> dates = new ArrayList<>();
        ArrayList<String> activities = new ArrayList<>();
        Date cDate = new Date();

        // initializes calendar with date + time of start of day
        // TODO implement more robust date method https://stackoverflow.com/a/39186531/7211749
        Calendar todayCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.ENGLISH);
        todayCal.setTime(cDate);
        todayCal.set(Calendar.HOUR_OF_DAY, 0);
        todayCal.set(Calendar.MINUTE, 0);
        todayCal.set(Calendar.SECOND, 0);
        todayCal.set(Calendar.MILLISECOND, 0);

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(
                    getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "activityLog")
            ));

            // TODO make this process more efficient than iterating through entire CSV file
            while (br.readLine() != null) {
                line = br.readLine(); // gets current CSV row
                lineSplit = line.split(","); // splits into String [] object
                cIterDateString = lineSplit[1]; // gets index 1 (Calendar string)
                try {
                    cIterActivityString = lineSplit[2]; // gets index 2 (Activity String)
                } catch (ArrayIndexOutOfBoundsException e) {
                    return null;
                }

                // creates new SDF with default (?) Calendar string format
                // SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                // parses string date from CSV row to Calendar object
                // cIterCal.setTime(sdf.parse(cIterDateString));

                // if the current CSV row's date is after the start time of day
                if (cIterCal.after(todayCal)) {
                    // add both the current CSV row date (calendar object) and activity to respective ArrayLists
                    dates.add(cIterCal);
                    activities.add(cIterActivityString);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return todayArray;
    }
}