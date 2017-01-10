package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.IBinder;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.widget.Toast;


public class Background extends Service {
    private SensorManager sensorManager;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // DEBUG ONLY
        Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();
        super.onCreate();
    }

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
}