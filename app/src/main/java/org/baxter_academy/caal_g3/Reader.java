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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Reader extends Service implements SensorEventListener {
    // constants for sensor stuff
    public int maxDataPoints = 200;
    public int curDataPoints = 0;
    public int sensorDelay = 50000; //ms

    // stuff for sensor calls
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // variables for sensor data
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
        sensorManager.registerListener(this, accelerometer, sensorDelay);
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
                stopSelf();
            }
        }
    }


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