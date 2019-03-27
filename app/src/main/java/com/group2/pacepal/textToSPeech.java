/*
Purpose: This is a test for android TTS. The goal is to create a scenario that is somewhat similar to how myMap wil work so that TTS will be
         ready to integrate with little trouble.
How it works: The android studio has a TTS engine that is easy to integrate into an app. This is the TextToSpeech engine. It receives the
          current context and begins initializing. The initialization is not instant, and a call to the speak function, which is the
          function that begins an utterance, can happen before the engine is ready. To avoid this ensure the engine has time to initialize
          before it is used.

          The engine has to be stopped as well. This is why there is the onpause engine closure. If this does not occurr the engine will likely make
          strange things happen. So avoid that by  using the stop function.

          There are options for the speak function. We can control pitch, speed, and the way the speak queue is emptied. So if some of that needs to
          be tweaked for the real app usage, we can do that without too much issue.

 */


package com.group2.pacepal;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.util.Locale;
import android.widget.Toast;
import java.util.Random;


public class textToSPeech extends Activity implements View.OnClickListener {
    TextToSpeech t1;
    String winString ;
    String loseString;
    String tieString;
    String stateArray [];
    Random rand = new Random();

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.text_to_speech);


        findViewById(R.id.speak).setOnClickListener(this);
        loseString = getString(R.string.cm_you_are_losing);
        winString = getString(R.string.cm_you_are_winning);
        tieString = getString(R.string.cm_you_are_tied);
        stateArray = new String[3];
        stateArray[0] = winString;
        stateArray[1] = loseString;
        stateArray[2] = tieString;
        //Log.v("String_Test", winString);




        t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Log.e("TTS", "Okay on speech language setting!");
                    t1.setLanguage(Locale.US);
                }
            }
        });




    }



    public void onPause(){
        if(t1 !=null){
            t1.stop();
            t1.shutdown();
        }
        super.onPause();
    }

    public int getRandomIndex() {
        int index = rand.nextInt(3);
        return index;
    }

    //simply activates the speech engine
    //It will read one of three strings from an array of strings that are loaded in from the resource file
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.speak :
                int whichLineIndex = getRandomIndex();
                String say = stateArray[whichLineIndex];

                t1.setSpeechRate((float) .6);
                int speechStatus = t1.speak(say, t1.QUEUE_FLUSH, null, "Test");

                if (speechStatus == TextToSpeech.ERROR) {
                    Log.e("TTS", "Error in converting Text to Speech!");
                }
                break;
        }
    }





}
