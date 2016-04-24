package br.com.halyson.ensharp.activity;

/**
 * Created by Jimin on 2015-07-14.
 */

import android.app.Activity;
import android.graphics.Typeface;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Date;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.fragment.Fragment2;
import br.com.halyson.ensharp.fragment.GCMBroadcastReceiver;
import br.com.halyson.ensharp.fragment.NavigationDrawerFragment;
import br.com.halyson.ensharp.fragment.UserException;


public class LockSleepActivity extends Activity implements OnInitListener {

    UserException ue=new UserException();
    Fragment2 maa=new Fragment2();
    NavigationDrawerFragment ndf=new NavigationDrawerFragment();
    HomeActivity homeActivity = new HomeActivity();

    boolean sleep_on=true;
    private TextToSpeech myTTS;
    String text="소리를 지르세요.";
    GCMBroadcastReceiver gbr=new GCMBroadcastReceiver();

    TextView tv_nowdec;
    TextView tv_targetdec;

    // ********************* Prevention Sleeping ************************* //
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    final Runnable updater = new Runnable() {
        public void run() {
            updateTv();
        }
    };
    final Handler mHandler = new Handler();

    long appStartTime, preventionCurrentTime, preventionElapsedTime;
    boolean preventionCheck;

    Double targetDecibel, currentDecibel, maxDecibel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.locksleep_activity);

        //Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //vibe.vibrate(700);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");

        tv_nowdec = (TextView) findViewById(R.id.tv_nowdec);
        tv_nowdec.setTypeface(myTypeface);
        tv_targetdec = (TextView) findViewById(R.id.tv_targetdec);
        tv_targetdec.setTypeface(myTypeface);

        /*Handler replay=new Handler(){
            public void handleMessage(Message msg)
            {
                myTTS = new TextToSpeech(this, this);
                myTTS.setSpeechRate(1);
            }
        };*/
        //autoend.sendEmptyMessageDelayed(0, 7000);

        myTTS = new TextToSpeech(this, this);
        myTTS.setSpeechRate(1);

        // ****************** Prevention Sleeping ********************** //

        appStartTime = preventionCurrentTime = preventionElapsedTime = 0;
        appStartTime = new Date().getTime() / 1000;
        preventionCheck = false;

        currentDecibel = maxDecibel = 0.0;
        targetDecibel = Double.valueOf(ndf.spsound_decibel);
        if (runner == null) {
            runner = new Thread() {
                public void run() {
                    while (runner != null && sleep_on==true) {
                        try {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) {
                        }
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        myTTS.shutdown();
    }

    public void btn_okOnclicked(View v){
        sleep_on=false;
        ue.SleepOnPopup=false;
        finish();
    }

    @Override
    public void onInit(int status) {
        String myText=text;
        myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);

    }

    // *********************** Decibel ****************************** //
    public void onResume() {
        super.onResume();
        startRecorder();
    }

    public void onPause() {
        super.onPause();
        stopRecorder();
    }

    public void startRecorder() {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null");
            try {
                mRecorder.prepare();
            } catch (java.io.IOException ioe) {
                Log.e("[Monkey]", "IOException: " + Log.getStackTraceString(ioe));

            } catch (SecurityException e) {
                Log.e("[Monkey]", "SecurityException: " + Log.getStackTraceString(e));
            }
            try {
                mRecorder.start();
            } catch (SecurityException e) {
                Log.e("[Monkey]", "SecurityException: " + Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }

    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv() {

        currentDecibel = getAmplitudeEMA();

        preventionCurrentTime = new Date().getTime() / 1000;
        preventionElapsedTime = preventionCurrentTime - appStartTime;

        if (currentDecibel >= maxDecibel) {
            maxDecibel = currentDecibel;
        }

        tv_targetdec.setText(String.format("목표 크기 : %d", ndf.spsound_decibel));
        tv_nowdec.setText(String.format("현재 크기 : %.0f", currentDecibel));

        if (currentDecibel >= targetDecibel) {//내가추가한거....실제로끄는거
            sleep_on=false;
            preventionCheck = true;
            ue.SleepOnPopup=false;
            finish();
        }

        if (preventionElapsedTime >= 60 && preventionCheck == false) {
            if (currentDecibel >= targetDecibel) {
                preventionCheck = true;
            }
        } else if (preventionCheck == true) {
            //Toast.makeText(getApplicationContext(), "Reminding", Toast.LENGTH_LONG).show();
            preventionCheck = false;
            appStartTime = new Date().getTime() / 1000;
            preventionCurrentTime = 0;
            maxDecibel = currentDecibel = 0.0;
        }
    }

    public double soundDb(double ampl) {
        return 20 * Math.log10(getAmplitudeEMA() / ampl);
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return (mRecorder.getMaxAmplitude());
        else
            return 0;
    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA / 100;
    }
}
