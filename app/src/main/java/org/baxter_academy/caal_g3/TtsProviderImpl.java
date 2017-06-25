package org.baxter_academy.caal_g3;

import android.content.Context;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by lyana on 25/06/2017.
 */

public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;

    public void init(Context context) {
        if (tts == null) {
            tts = new TextToSpeech(context, this);
        }
    }

    @Override
    public void say(String sayThis) {
        tts.speak(sayThis, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        Locale loc = new Locale("de", "", "");
        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        }
    }

    public void shutdown() {
        tts.shutdown();
    }}