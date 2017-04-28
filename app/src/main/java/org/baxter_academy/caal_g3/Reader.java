package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class Reader extends Service implements SensorEventListener {
    // constants for sensor stuff
    public int maxDataPoints = 200;
    public int curDataPoints = 0;
    public double sensorRate = 20; //unit?

    // stuff for sensor calls
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // variables for sensor data
    private long lastUpdate = 0;
    // sets up file object for storing accelerometer data
    public String rawDataFilename = "rawData";
    public BufferedWriter writer = null;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Puts a message on the screen
        System.out.println("***********************STARTED READER***********************");
        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
        super.onCreate();

        // opens file for writing
        try {
            writer = new BufferedWriter(
                    new FileWriter(new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), rawDataFilename)
                    ));

        } catch (IOException e) {
            e.printStackTrace();
        }

        // sensor info
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 50000);
    }
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // gets uptime of system
            long curTime = SystemClock.elapsedRealtime();

            curDataPoints = curDataPoints + 1;
            System.out.println(curDataPoints + "/" + maxDataPoints + ", " + curTime);

            // saves current readings to a temporary string in memory
            String toWrite = "User" + "," + "NoLabel" + "," + curTime + "," + x + "," + y + "," + z + ";"; //fixme hardcoded user variable, corresponds w/ StandAloneFeat

            // writes string to file
            try {
                if (writer != null) {
                    writer.write(toWrite);
                    writer.newLine();
                    writer.flush();

                } else {
                    System.out.println("file reference is null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            // if its time to stop reading sensor data
            if (curDataPoints >= maxDataPoints) {

                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // (debug) finds elapsed time
                try {
                    InputStream inputStream = getBaseContext().openFileInput(rawDataFilename);

                    if ( inputStream != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        String ret;
                        ret = stringBuilder.toString();
                        System.out.print(ret);
                    }
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }

                stopSelf();
            }
        }
    }

    // called by system every time a sensor event gets triggered
/*    public void onSensorChanged(SensorEvent sensorEvent) { //fixme commented out to rewrite
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];

            // gets uptime of system
            long curTime = SystemClock.elapsedRealtime();

            // if enough time has passed since last data point was collected
            if ((curTime - lastUpdate) > sensorRate) {
                //long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                curDataPoints = curDataPoints + 1;

                System.out.println(curDataPoints + "/" + maxDataPoints);

                // saves current readings to a temporary string in memory
                String toWrite = "User" + "," + "NoLabel" + "," + curTime + "," + x + "," + y + "," + z + ";"; //fixme hardcoded user variable, corresponds w/ StandAloneFeat

                // writes string to file
                try {
                    if (writer != null) {
                        writer.write(toWrite);
                        writer.newLine();
                        writer.flush();

                    } else {
                        System.out.println("file reference is null");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // if its time to stop reading sensor data
                if (curDataPoints >= maxDataPoints) {

                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stopSelf();
                    }
            }
        }
    }*/

    // Sensor requirements
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Called when service stopped
    public void onDestroy() {
        System.out.println("Stopped Reader");
        sensorManager.unregisterListener(this); // stops sensorManager

        Log.d("sender", "Broadcasting message");
        Intent intent = new Intent("FinishedWork");
        // You can also include some extra data.
        intent.putExtra("message", "collected " + maxDataPoints + " data points");
        intent.putExtra("status", "Reader finished");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}