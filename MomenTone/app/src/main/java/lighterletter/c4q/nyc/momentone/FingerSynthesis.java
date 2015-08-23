package lighterletter.c4q.nyc.momentone;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by c4q-john on 8/22/15.
 */
public class FingerSynthesis extends Activity implements View.OnTouchListener {

    AudioSynthesisTask audioSynth;
    static final float BASE_FREQUENCY = 440;
    float synth_frequency = BASE_FREQUENCY;
    static final float baseAmplitude = 50;
    float amplitude = baseAmplitude; //variable mapped to the y axis of the event input, in this case the touch sensor.
    boolean play = false; // used to determine when we should actually be playing sound or not base on touch event.

    SensorEvent event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        Accelerometer accelerometer = new Accelerometer();

       amplitude =  baseAmplitude *  accelerometer.x;
               View mainView = this.findViewById(R.id.MainView);
        mainView.setOnTouchListener(this);

        audioSynth = new AudioSynthesisTask();
        audioSynth.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        play = false;
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                play = true;
                synth_frequency = event.getX(); //pitch
                amplitude = event.getY();//volume
                Log.v("FREQUENCY", ""+synth_frequency);
                break;
            case MotionEvent.ACTION_MOVE:
                play = true;
                synth_frequency = event.getX();
                amplitude =  event.getY();//
                Log.v("FREQUENCY", ""+synth_frequency);
                break;
            case MotionEvent.ACTION_UP:
                play = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }
    private class AudioSynthesisTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... params) {
            final int SAMPLE_RATE = 44100;

            int minSize = AudioTrack.getMinBufferSize( //Sample size. Individual sample.
                    SAMPLE_RATE,                    // Numerical cycles per second
                    AudioFormat.CHANNEL_OUT_MONO,   // Channel to output
                    AudioFormat.ENCODING_PCM_16BIT  // pulse code modulation giving you
                                                    // encoding at 16 bits per cycle to define a sample rate.
            );
            AudioTrack audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minSize,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            //test track
            AudioTrack audioTrack2 = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minSize,
                    AudioTrack.MODE_STREAM);
            audioTrack2.play();

            AudioTrack audioTrack3 = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    minSize,
                    AudioTrack.MODE_STREAM);
            audioTrack3.play();

            short[] sample_buffer = new short[minSize];
            float angle = 0;



            while(true){
                if (play)
                {
                    for ( int i = 0; i < sample_buffer.length; i++)
                    {
                        float angular_frequency = (float) (2*Math.PI) * synth_frequency / SAMPLE_RATE;
                        angle += angular_frequency;
                        sample_buffer[i] = (short) (amplitude * ((float)Math.sin(angle)));
                    }

                    audioTrack.write(sample_buffer, 0 , sample_buffer.length);
//                    synth_frequency = synth_frequency - 20; //effect test.
                    //audioTrack2.write(sample_buffer,0,sample_buffer.length);//test track
                }
                if (play)
                {
                    for ( int i = 0; i < sample_buffer.length; i++) {
                        float angular_frequency =  (synth_frequency++ % 6) <3 ? 3 :0; //(float) (2 * Math.PI) * synth_frequency / SAMPLE_RATE;
                        angle += angular_frequency;
                        sample_buffer[i] = (short) (amplitude * ((float) Math.cos(angle)));


                        //sample_buffer = (short)(amplitude * Math.Sign(Math.sin(angle * i)));
                    }
//                    audioTrack.write(sample_buffer, 0 , sample_buffer.length);
//                    synth_frequency = synth_frequency - 20; //effect test.
                    audioTrack2.write(sample_buffer,0,sample_buffer.length);//test track
                }
                if (play)
                {
                    for ( int i = 0; i < sample_buffer.length; i++)
                    {


                        float angular_frequency = (synth_frequency++ % 6) <3 ? 3 :0;
                        //(short) (2*Math.PI) * synth_frequency / SAMPLE_RATE;
                        angle += angular_frequency;
                        sample_buffer[i] = (short) (amplitude * ((float)Math.cos(angle)));
                    }
//                    audioTrack.write(sample_buffer, 0 , sample_buffer.length);
//                    synth_frequency = synth_frequency - 20; //effect test.
                    audioTrack3.write(sample_buffer,0,sample_buffer.length);//test track
                }
            }
        }
    }
}