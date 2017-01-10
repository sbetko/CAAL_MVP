package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;


public class Background extends Service implements SensorEventListener {
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
        sensorManager.registerListener(this, accelerometer , SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        //prints to debug
        System.out.println("Test");

        System.out.println(x);
        System.out.println(y);
        System.out.println(z);

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;

                //prints to debug
                System.out.println("Test");

                System.out.println(x);
                System.out.println(y);
                System.out.println(z);

            }
        }
    }

    //FROM HERE DOWN IS JUST SENSOR REQUIREMENTS
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        /**
    }
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }
**/
}
/**
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
