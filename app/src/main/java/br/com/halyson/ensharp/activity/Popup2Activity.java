package br.com.halyson.ensharp.activity;

/**
 * Created by Jimin on 2015-07-14.
 */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.fragment.Fragment2;
import br.com.halyson.ensharp.fragment.GCMBroadcastReceiver;
import br.com.halyson.ensharp.fragment.MyService;
import br.com.halyson.ensharp.fragment.UserException;


public class Popup2Activity extends Activity implements OnInitListener {

    private TextToSpeech myTTS;
    String text;
    GCMBroadcastReceiver gbr = new GCMBroadcastReceiver();

    Fragment2 maa = new Fragment2();
    MyService ms = new MyService();
    UserException ue = new UserException();
    HomeActivity homeActivity = new HomeActivity();

    String strUrl;

    public static TextView tv_remainderdis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popup2_activity);

        strUrl = "http://openapi.its.go.kr/api/NCCTVInfo?key=1436770430812&ReqType=2&MinX=127.100000&MaxX=128.890000&MinY=34.100000&MaxY=39.100000";
        //Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //vibe.vibrate(700);
        tv_remainderdis=(TextView) findViewById(R.id.tv_remainderdis);
        tv_remainderdis.setText("");

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");

        TextView textView = (TextView) findViewById(R.id.tv_content);
        textView.setTypeface(myTypeface);

        Intent intent = getIntent();
        /*String data = intent.getStringExtra("a");
        textView.setText(data);*/
        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        ue.OnPopup = true;

        String from = intent.getStringExtra("from");
        if (from == null) {
            //Log.d(TAG, "from is null.");
            return;
        }


        String command = intent.getStringExtra("command");
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");

        if(UserException.calDistance(maa.currentacc_lat, maa.currentacc_lon, maa.current_lat, maa.current_lon)<1000.0)
        {
            String dis = String.format("%.0f", UserException.calDistance(maa.currentacc_lat, maa.currentacc_lon, maa.current_lat, maa.current_lon));
            text = "전방 "+dis+"m 앞 "+data;
        }
        else if(UserException.calDistance(maa.currentacc_lat, maa.currentacc_lon, maa.current_lat, maa.current_lon)>=1000.0)
        {
            String dis = String.format("%.1f", UserException.calDistance(maa.currentacc_lat, maa.currentacc_lon, maa.current_lat, maa.current_lon) / 1000.0);
            text = "전방 "+dis+"km 앞 "+data;
        }
        textView.setText(text);

        myTTS = new TextToSpeech(this, this);
        //myTTS.setLanguage(Locale.KOREAN);
        myTTS.setSpeechRate(1);

        /*Handler autoend = new Handler() {
            public void handleMessage(Message msg) {
                ue.OnPopup = false;
                finish();   // activity ����
            }
        };
        autoend.sendEmptyMessageDelayed(0, 7000);*/

        Thread thread = new Thread(){
            public void run(){
                while(true){
                    try{
                        sleep(1000);
                    }catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                }
            }
        };
        thread.start();

        Thread threadend = new Thread(){
            public void run(){
                while(true){
                    try{
                        sleep(100);
                    }catch (Exception e) {
                        // TODO: handle exception
                        e.printStackTrace();
                    }
                    handlerend.sendEmptyMessage(0);
                }
            }
        };
        threadend.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTTS.shutdown();
    }

    public void btn_okOnclicked(View v) {
        ue.OnPopup = false;
        finish();
    }

    public void btn_cctvOnclicked(View v) {
        Intent intent_cctv = new Intent(getApplicationContext(), CctvActivity.class);
        startActivity(intent_cctv);
        ue.CCTVOnPopup = true;
        maa.tocctv=true;
        finish();
    }


    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            maa.acctome = UserException.calDistance(maa.currentacc_lat, maa.currentacc_lon, maa.current_lat, maa.current_lon);
            if (maa.acctome <= 0.0 || maa.acctome >= 9000.0) {
                tv_remainderdis.setText("");
            } else if (maa.acctome < 1000.0) {
                tv_remainderdis.setText(String.format("%.0f", maa.acctome) + "m");
            } else {
                tv_remainderdis.setText(String.format("%.1f", maa.acctome / 1000.0) + "km");
            }
        }

    };

    private Handler handlerend = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(maa.acctome<50.0) {
                ue.OnPopup = false;
                finish();
            }
        }
    };

    @Override
    public void onInit(int status) {
        String myText = text;
        myTTS.speak(myText, TextToSpeech.QUEUE_FLUSH, null);
    }
}
