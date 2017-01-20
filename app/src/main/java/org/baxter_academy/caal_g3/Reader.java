package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Reader extends Service implements SensorEventListener {
    // constants for sensor management
    public int maxDataPoints = 25;
    public int curDataPoints = 0;
    public int sensorRate = 50;

    // stuff for sensor calls
    private SensorManager sensorManager;
    private Sensor accelerometer;

    // variables for sensor data
    private long lastUpdate = 0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // Puts a message on the screen
        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
        super.onCreate();

        // sensor info
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);


    }

    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = SystemClock.elapsedRealtime();

            // if its time for gathering the next data point
            if ((curTime - lastUpdate) > sensorRate) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                curDataPoints = curDataPoints + 1;
                //prints to debug
                System.out.println("Test");
                System.out.println(x);
                System.out.println(y);
                System.out.println(z);
                System.out.println(curDataPoints + "/" + maxDataPoints);

                // saves current readings to a temporary string in memory
                String toWrite = x + "," + y + "," + z + "," + curTime + ";" + System.getProperty("line.separator");

                // if its time to stop reading sensor data
                if (curDataPoints >= maxDataPoints) {
                    /** DOES ALL FILE WORK (NO PRIOR SETUP) **/
                    // sets up file object for storing accelerometer data
                    String FILENAME = "classification";
                    BufferedWriter writer = null;

                    // opens file for writing
                    try {
                        writer = new BufferedWriter(
                                new FileWriter(new File(getFilesDir(), FILENAME)
                                ));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // writes to file
                    try {
                        if (writer != null) {
                            writer.write(toWrite);
                            writer.newLine();
                            writer.flush();
                            writer.close();
                            System.out.println("file reference is not null");
                        } else {
                            System.out.println("file reference is null");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    stopSelf();
                    }
            }
        }
    }


    // Sensor requirements
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    // Called when service stopped
    public void onDestroy() {
        Toast.makeText(this, "Stopped Reader", Toast.LENGTH_SHORT).show(); // Pops up message
        System.out.println("Stopped Reader");
        sensorManager.unregisterListener(this); // stops sensorManager
        Intent cleanerIntent = new Intent(this.getBaseContext(), Cleaner.class);
        startService(cleanerIntent);
        super.onDestroy();
    }

/**
  * experimental method
    private void getAccelerometer(SensorEvent event) {
        float[] values = event.values;
        // Movement
        float x = values[0];
        float y = values[1];
        float z = values[2];
        System.out.println(x);
        System.out.println(y);
        System.out.println(z);

        long actualTime = System.currentTimeMillis();

    }
 **/
}