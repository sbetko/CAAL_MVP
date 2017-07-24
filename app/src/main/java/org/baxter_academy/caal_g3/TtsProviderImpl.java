package org.baxter_academy.caal_g3;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;

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
        Locale loc = new Locale("en", "", "");
        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        }
        Voice voice = new Voice("main", Locale.US, Voice.QUALITY_VERY_HIGH, Voice.LATENCY_NORMAL, true, null);
        tts.setVoice(voice);
    }

    public void shutdown() {
        tts.shutdown();
    }}