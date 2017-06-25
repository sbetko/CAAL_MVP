package org.baxter_academy.caal_g3;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;

/**
 * Created by lyana on 24/06/2017.
 */

public class SpeechService extends Service {

    public TextToSpeech tts;
    public String activity;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        System.out.println("************ Started SpeechService");
        Bundle bundle = intent.getExtras();
        activity = bundle.getString("Activity");

        Context context = getApplicationContext();
        TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
        if (ttsProviderImpl != null) {
            ttsProviderImpl.init(context);
            ttsProviderImpl.say(activity);
        }
        return START_REDELIVER_INTENT;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

}