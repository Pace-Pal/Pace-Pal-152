package com.group2.pacepal

import android.content.Context
import android.util.Log
import android.speech.tts.TextToSpeech
import java.util.*

data class TextToSpeech (val context: Context?){

    private var tts: TextToSpeech? = null
    private var readyToSpeak = 0


    init {
        readySpeechEngine()
    }


    fun readySpeechEngine() {

        tts = TextToSpeech(context, TextToSpeech.OnInitListener { status ->
                if (status != TextToSpeech.ERROR){
                    //if there is no error then set language
                    tts?.language = Locale.US
                }
            readyToSpeak = 1
        })

    }



    fun speak(phrase : String) {
        if(readyToSpeak == 1)
            tts?.speak(phrase, TextToSpeech.QUEUE_FLUSH, null)
        else
            Log.v("TTS", "Engine Needs to Finish Loading")
    }

    fun pause() {
        if(tts !=null){
            tts?.stop();
            tts?.shutdown();
        }
    }
}