package org.baxter_academy.caal_g3;

/**
 * Created by Baxter on 1/13/2017.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

//TODO write pre-processing / ARFF formatting functions
public class Cleaner extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onDestroy() {
        Intent wekaClassifierIntent = new Intent(this.getApplicationContext(), WekaClassifier.class);
        startService(wekaClassifierIntent);

    }
}