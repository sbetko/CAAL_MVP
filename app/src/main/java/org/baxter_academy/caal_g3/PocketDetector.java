package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.DisplayManager;
import android.os.BatteryManager;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;

/**
 * Created by lyana on 27/07/2017.
 */

public class PocketDetector extends Service implements SensorEventListener {

    private SensorManager sense;
    private Sensor proximitySensor;

    private float proximity;
    private boolean charging;
    private boolean screenOn;

    private boolean proxFinished;
    private boolean screenFinished;
    private boolean chargeFinished;

    public void onCreate() {

        sense = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sense.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sense.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);

        this.registerReceiver(chargingReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        screenOn();

        //REGISTER SCREEN ON LISTENER
        //REGISTER CHARGING LISTENER

        System.out.println("started pocket detector******************************");
    }

    public void sync() {
        if (proxFinished && screenFinished && chargeFinished) {
            if (proximity == proximitySensor.getMaximumRange() ||
                    charging || screenOn) {
                //restart core service chain in 10 seconds
                Intent intent = new Intent("FinishedWork");
                intent.putExtra("status", "PocketDetector finished in pocket");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                stopSelf();
            } else {
                // continues along with core service chain
                Intent intent = new Intent("FinishedWork");
                intent.putExtra("status", "PocketDetector finished");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

                stopSelf();
            }
        }

    }

    private void screenOn() {
        DisplayManager dm = (DisplayManager) this.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : dm.getDisplays()) {
            if (display.getState() != Display.STATE_OFF) {
                screenOn = true; //TODO debug
            } else {
                screenOn = false;
            }
        }
        screenFinished = true;
        sync();

    }

    BroadcastReceiver chargingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            charging = false;
            chargeFinished = true;
            sync();
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximity = event.values[0];
            proxFinished = true;
            sync();
            sense.unregisterListener(this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        sense.unregisterListener(this);
        this.unregisterReceiver(chargingReceiver);
    }
}
