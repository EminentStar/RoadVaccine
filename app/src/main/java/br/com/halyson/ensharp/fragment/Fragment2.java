package br.com.halyson.ensharp.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Vibrator;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.activity.CctvActivity;
import br.com.halyson.ensharp.activity.EnrollCCTV;
import br.com.halyson.ensharp.activity.EnrollData;
import br.com.halyson.ensharp.activity.EnrollDataCaution;
import br.com.halyson.ensharp.activity.First_introductionActivity;
import br.com.halyson.ensharp.activity.HomeActivity;
import br.com.halyson.ensharp.activity.IntroductionActivity;
import br.com.halyson.ensharp.activity.MyCctvActivity;
import br.com.halyson.ensharp.activity.PopupActivity;
import br.com.halyson.ensharp.activity.ScrollingImageView;

import android.app.ActivityManager.RunningAppProcessInfo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.widget.ToggleButton;

import static java.lang.Math.abs;
import static java.lang.Math.floor;

/**
 * Created by halyson on 18/12/14.
 */
public class Fragment2 extends Fragment {
    private static final String TAG = "Fragment2";

    public static Fragment2 newInstance() {
        return new Fragment2();
    }

    UserException ue = new UserException();
    NavigationDrawerFragment ndf = new NavigationDrawerFragment();

    public static boolean isAgain=false;
    public static int spdis_accident, spdis_caution, sptime_prevention;
    public static boolean onoff_accident, onoff_caution, onoff_prevention, onoff_myinfo, onoff_cctv;

    public static boolean network_onoff;
    public static boolean tocctv = false;

    public static Double acctome = -1.0;

    public static boolean isAccident = false;
    public static boolean isCaution = false;

    public static boolean s1, s2, s3;

    public static ImageButton btn;
    public static ImageButton btn_send;
    public static ImageButton btn_mycctv;
    public static ImageButton btn_q;

    Drawable drawable;

    private View mViewFragment2;

    public static LocationManager manager;

    int type_num;
    public static int type_sendnum;
    public static int type_cctvnum;

    int count = 0;

    final MediaPlayer mp = new MediaPlayer();
    /*************************
     * 준큐
     ***********/
    //Web Server 호출을 위한 인스턴스들
    phpInsert task_insert;
    phpInitialApi task_initial;
    public static phpSuddenStop task_suddenStop;

    public static String xPoint;
    public static String yPoint;

    String serial_number;
    /****************************************/

    public static Uri resultUri;
    int videoSequence;

    public static String cctv_uri;
    public static String cctv_lat;
    public static String cctv_lon;

    public static String regId;

    public static boolean updown = true;

    public static Double current_lat = 0.0;
    public static Double current_lon = 0.0;

    public static Double currentacc_lat = 0.0;
    public static Double currentacc_lon = 0.0;

    public static boolean OnService = false;
    public static boolean OnSleepService = false;

    public static ArrayList<EnrollData> list_data = new ArrayList<EnrollData>();

    public static ArrayList<EnrollDataCaution> list_coutiondata = new ArrayList<EnrollDataCaution>();

    public static ArrayList<EnrollCCTV> cctv_data = new ArrayList<EnrollCCTV>();


    Handler handler = new Handler();


    Intent myIntent;
    Intent myIntent2;
    Intent myIntent3;

    /********************************/
    AnimationDrawable iv_mainAnimation;

    AudioManager clsAudioManager;
    Vibrator vibe;
    String strUrl;
    //AudioManager clsAudioManager = (AudioManager) mViewFragment2.m_clsContext.getSystemService(Context.AUDIO_SERVICE);

    ImageView maincar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mViewFragment2 = inflater.inflate(R.layout.fragment_2, container, false);
        list_data.clear();
        cctv_data.clear();

        maincar = (ImageView) mViewFragment2.findViewById(R.id.maincar);
        maincar.setBackgroundResource(R.drawable.iv_main_movingcar);
        iv_mainAnimation = (AnimationDrawable) maincar.getBackground();
        iv_mainAnimation.stop();

        clsAudioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        vibe = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        getActivity().registerReceiver(this.NetworkChangeReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        resultUri = null;
        videoSequence = 1;
        strUrl = "http://openapi.its.go.kr/api/NCCTVInfo?key=1436770430812&ReqType=2&MinX=127.100000&MaxX=128.890000&MinY=34.100000&MaxY=39.100000";

        serial_number = getDeviceSerialNumber();

        //단말등록
        try {
            registerDevice();

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        network_onoff = isNetworkState(getActivity());

        if (isNetworkState(getActivity()) == true) {

        } else {
            println("네트워크 상태 확인, 졸음 기능만 사용 가능");
        }


        btn = (ImageButton) mViewFragment2.findViewById(R.id.imageButton);
        drawable = getResources().getDrawable(R.drawable.img_driveoff);
        btn.setImageDrawable(drawable);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count == 0) {
                    SetMainFlag();
                    type_num = NowType();

                    switch (type_num) {
                        case 1:
                            println("설정창을 확인하세요");
                            break;
                        case 2:
                            println("네트워크 상태를 확인하세요");
                            break;
                        case 3:
                            println("GPS 상태를 확인하세요");
                            break;
                        case 4:
                            println("네트워크, GPS 상태를 확인하세요");
                            break;
                        case 5:
                            switch (clsAudioManager.getRingerMode()) {
                                case AudioManager.RINGER_MODE_VIBRATE:
                                    vibe.vibrate(700);
                                    // 진동 모드
                                    break;
                                case AudioManager.RINGER_MODE_NORMAL:
                                    // 소리 모드
                                    startSound();
                                    break;
                                case AudioManager.RINGER_MODE_SILENT:
                                    // 무음 모드
                                    break;
                            }
                            SetMainAnimationStart();
                            iv_mainAnimation.start();
                            drawable = getResources().getDrawable(R.drawable.img_driveonon);
                            btn.setImageDrawable(drawable);
                            SetMainFlag();
                            println("졸음 알림 기능만 시작합니다");
                            myIntent3 = new Intent(getActivity(), SleepService.class);
                            OnSleepService = true;
                            getActivity().startService(myIntent3);
                            s3 = true;
                            count = 1;
                            break;
                        case 6:
                            switch (clsAudioManager.getRingerMode()) {
                                case AudioManager.RINGER_MODE_VIBRATE:
                                    // 진동 모드
                                    vibe.vibrate(700);
                                    break;
                                case AudioManager.RINGER_MODE_NORMAL:
                                    // 소리 모드
                                    startSound();
                                    break;
                                case AudioManager.RINGER_MODE_SILENT:
                                    // 무음 모드
                                    break;
                            }
                            SetMainAnimationStart();
                            iv_mainAnimation.start();
                            drawable = getResources().getDrawable(R.drawable.img_driveonon);
                            btn.setImageDrawable(drawable);

                            getInitialAPIToDevice();
                            SetMainFlag();

                            myIntent = new Intent(getActivity(), OnService.class);
                            myIntent2 = new Intent(getActivity(), MyService.class);

                            OnService = true;

                            println("주행을 시작합니다");
                            // 서비스를 시작합니다.
                            getActivity().startService(myIntent);
                            getActivity().startService(myIntent2);

                            s1 = true;
                            s2 = true;

                            //getInitialAPIToDevice();
                            count = 1;
                            break;
                        case 7:
                            switch (clsAudioManager.getRingerMode()) {
                                case AudioManager.RINGER_MODE_VIBRATE:
                                    // 진동 모드
                                    vibe.vibrate(700);
                                    break;
                                case AudioManager.RINGER_MODE_NORMAL:
                                    // 소리 모드
                                    startSound();
                                    break;
                                case AudioManager.RINGER_MODE_SILENT:
                                    // 무음 모드
                                    break;
                            }
                            SetMainAnimationStart();
                            iv_mainAnimation.start();
                            drawable = getResources().getDrawable(R.drawable.img_driveonon);
                            btn.setImageDrawable(drawable);

                            getInitialAPIToDevice();
                            SetMainFlag();

                            myIntent = new Intent(getActivity(), OnService.class);
                            myIntent2 = new Intent(getActivity(), MyService.class);
                            myIntent3 = new Intent(getActivity(), SleepService.class);


                            OnService = true;
                            OnSleepService = true;
                            println("주행을 시작합니다");
                            // 서비스를 시작합니다.
                            getActivity().startService(myIntent);
                            getActivity().startService(myIntent2);
                            getActivity().startService(myIntent3);

                            s1 = true;
                            s2 = true;
                            s3 = true;

                            //getInitialAPIToDevice();
                            count = 1;
                            break;
                    }
                } else if (count == 1) {
                    list_data.clear();
                    list_coutiondata.clear();
                    SetMainFlag();
                    StopService();
                }
            }
        });

        btn_q = (ImageButton) mViewFragment2.findViewById(R.id.btn_q);
        btn_q.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), IntroductionActivity.class));
            }
        });

        btn_send = (ImageButton) mViewFragment2.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMainFlag();
                type_sendnum = NowSendType();

                switch (type_sendnum) {
                    case 1:
                        println("네트워크, GPS, 설정창 상태를 확인하세요");
                        break;
                    case 2:
                        println("네트워크, GPS 상태를 확인하세요");
                        break;
                    case 3:
                        println("GPS, 설정창 상태를 확인하세요");
                        break;
                    case 4:
                        println("GPS 상태를 확인하세요");
                        break;
                    case 5:
                        println("네트워크, 설정창 상태를 확인하세요");
                        break;
                    case 6:
                        println("네트워크 상태를 확인하세요");
                        break;
                    case 7:
                        println("설정창 상태를 확인하세요");
                        break;
                    case 8:
                        println("내 위치를 전송했습니다");
                        xPoint = String.valueOf(current_lon);
                        yPoint = String.valueOf(current_lat);
                        sendSuddenStoppedPoint();
                        isAgain = true;
                        break;
                }
            }
        });

        btn_mycctv = (ImageButton) mViewFragment2.findViewById(R.id.btn_mycctv);
        btn_mycctv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetMainFlag();
                type_cctvnum = NowCctvType();

                switch (type_cctvnum) {
                    case 1:
                        println("네트워크, GPS 상태를 확인하세요");
                        break;
                    case 2:
                        println("GPS 상태를 확인하세요");
                        break;
                    case 3:
                        println("네트워크 상태를 확인하세요");
                        break;
                    case 4:
                        println("GPS가 불안정하여 위치를 알 수 없습니다.");
                        break;
                    case 5:
                        if (isNetworkState(getActivity()) == true) {
                            if (cctv_data.size() == 0) {
                                /*try {
                                    if (strUrl != null && strUrl.length() > 0) {
                                        ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                                        if (netInfo != null && netInfo.isConnected()) {
                                            new DownloadWebpageText().execute(strUrl);        // html 다운로드 쓰레드 기동

                                        } else {
                                            throw new Exception();
                                        }
                                    } else {
                                        throw new Exception();
                                    }
                                } catch (Exception e) {
                                }*/
                            }
                            /////////////////////////////////////////////////////
                        } else {
                        }
                        Intent intent_mycctv = new Intent(getActivity().getApplicationContext(), MyCctvActivity.class);
                        startActivity(intent_mycctv);

                        ue.CCTVOnPopup = true;
                        break;
                }
            }
        });

        return mViewFragment2;
    }


    public void startSound() {
        try {
            mp.reset();
            AssetFileDescriptor afd;
            afd = getActivity().getAssets().openFd("engine_start.mp3");
            mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mp.prepare();
            mp.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void SetMainAnimationStart() {
        ((ScrollingImageView) mViewFragment2.findViewById(R.id.myScroll1)).start();
        ((ScrollingImageView) mViewFragment2.findViewById(R.id.myScroll2)).start();
    }

    public void SetMainAnimationStop() {
        ((ScrollingImageView) mViewFragment2.findViewById(R.id.myScroll1)).stop();
        ((ScrollingImageView) mViewFragment2.findViewById(R.id.myScroll2)).stop();
    }


    private static String getDeviceSerialNumber() {
        try {
            return (String) Build.class.getField("SERIAL").get(null);
        } catch (Exception ignored) {
            return null;
        }
    }

    public int NowCctvType() {
        if (isOnGps() == false && isNetworkState(getActivity()) == false) {
            return 1;//GPS, 네트워크
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true) {
            return 2;//GPS
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false) {
            return 3;//네트워크
        }
        /*else if (isOnGps() == true && isNetworkState(getActivity()) == true) {
            return 4;//cctv
        }*/
        else if (isOnGps() == true && isNetworkState(getActivity()) == true) {
            return 5;//cctv
        }
        return 1;
    }

    public int NowSendType() {
        if (isOnGps() == false && isNetworkState(getActivity()) == false && onoff_myinfo == false) {
            return 1;//전부 체크해야함
        } else if (isOnGps() == false && isNetworkState(getActivity()) == false && onoff_myinfo == true) {
            return 2;//GPS, 네트워크
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true && onoff_myinfo == false) {
            return 3;//GPS, 토글
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true && onoff_myinfo == true) {
            return 4;//GPS
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false && onoff_myinfo == false) {
            return 5;//네트워크, 토글
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false && onoff_myinfo == true) {
            return 6;//네트워크
        } else if (isOnGps() == true && isNetworkState(getActivity()) == true && onoff_myinfo == false) {
            return 7;//토글
        } else if (isOnGps() == true && isNetworkState(getActivity()) == true && onoff_myinfo == true) {
            return 8;//전송
        }
        return 1;
    }

    public int NowType() {
        if ((onoff_accident == false && onoff_caution == false) && onoff_prevention == false) {
            return 1;//아무것도 실행되지않음
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false && (onoff_accident == true || onoff_caution == true) && onoff_prevention == true) {
            return 2;//네트워크상태 확인
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false && (onoff_accident == true || onoff_caution == true) && onoff_prevention == false) {
            return 2;//네트워크상태 확인
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true && (onoff_accident == true || onoff_caution == true) && onoff_prevention == true) {
            return 3;//GPS상태 확인
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true && (onoff_accident == true || onoff_caution == true) && onoff_prevention == false) {
            return 3;//GPS상태 확인
        } else if (isOnGps() == false && isNetworkState(getActivity()) == false && (onoff_accident == true || onoff_caution == true) && onoff_prevention == true) {
            return 4;//네트워크, GPS둘다 확인
        } else if (isOnGps() == false && isNetworkState(getActivity()) == false && (onoff_accident == true || onoff_caution == true) && onoff_prevention == false) {
            return 4;//네트워크, GPS둘다 확인
        } else if (isOnGps() == true && isNetworkState(getActivity()) == true && (onoff_accident == false && onoff_caution == false) && onoff_prevention == true) {
            return 5;//졸음만
        } else if (isOnGps() == true && isNetworkState(getActivity()) == false && (onoff_accident == false && onoff_caution == false) && onoff_prevention == true) {
            return 5;//졸음만
        } else if (isOnGps() == false && isNetworkState(getActivity()) == true && (onoff_accident == false && onoff_caution == false) && onoff_prevention == true) {
            return 5;//졸음만
        } else if (isOnGps() == false && isNetworkState(getActivity()) == false && (onoff_accident == false && onoff_caution == false) && onoff_prevention == true) {
            return 5;//졸음만
        } else if (isOnGps() == true && isNetworkState(getActivity()) == true && (onoff_accident == true || onoff_caution == true) && onoff_prevention == false) {
            return 6;//OOX
        } else if (isOnGps() == true && isNetworkState(getActivity()) == true && (onoff_accident == true || onoff_caution == true) && onoff_prevention == true) {
            return 7;//전부실행
        }
        return 1;
    }

    public boolean isOnGps() {
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    public BroadcastReceiver NetworkChangeReceiver = new BroadcastReceiver() {
        //public class NetworkChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(final Context context, final Intent intent) {

            if (isRunningProcess(getActivity(), "br.com.halyson.ensharp")) { //앱이 실행중이면
                String status = NetworkUtil.getConnectivityStatusString(context);

                if (status.equals("Wifi enabled")) {
                } else if (status.equals("Mobile data enabled")) {
                } else if (status.equals("Not connected to Internet")) {
                    SetMainAnimationStop();
                    iv_mainAnimation.stop();
                    drawable = getResources().getDrawable(R.drawable.img_driveoff);
                    btn.setImageDrawable(drawable);
                    StopServiceNet();
                    SetMainFlag();
                }
            }
        }
    };

    public static boolean isRunningProcess(Context context, String packageName) { //앱이 실행중인지

        boolean isRunning = false;

        ActivityManager actMng = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo rap : list) {
            if (rap.processName.equals(packageName)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public void StopServiceNet() {
        if (type_num == 5) {

        } else if (type_num == 6) {
            SetMainAnimationStop();
            iv_mainAnimation.stop();
            drawable = getResources().getDrawable(R.drawable.img_driveoff);
            btn.setImageDrawable(drawable);
            OnService = false;
            println("주행을 종료합니다");

            getActivity().stopService(myIntent);
            getActivity().stopService(myIntent2);

            s1 = false;
            s2 = false;

            count = 0;
        } else if (type_num == 7) {
            SetMainAnimationStop();
            iv_mainAnimation.stop();
            drawable = getResources().getDrawable(R.drawable.img_driveoff);
            btn.setImageDrawable(drawable);
            OnService = false;
            OnSleepService = false;
            println("주행을 종료합니다");

            getActivity().stopService(myIntent);
            getActivity().stopService(myIntent2);
            getActivity().stopService(myIntent3);

            s1 = false;
            s2 = false;
            s3 = false;

            count = 0;
        }
    }

    public void StopService() {
        if (type_num == 5) {
            SetMainAnimationStop();
            iv_mainAnimation.stop();
            drawable = getResources().getDrawable(R.drawable.img_driveoff);
            btn.setImageDrawable(drawable);
            OnSleepService = false;
            println("주행을 종료합니다");
            getActivity().stopService(myIntent3);

            s3 = false;

            count = 0;
        } else if (type_num == 6) {
            SetMainAnimationStop();
            iv_mainAnimation.stop();
            drawable = getResources().getDrawable(R.drawable.img_driveoff);
            btn.setImageDrawable(drawable);
            OnService = false;
            println("주행을 종료합니다");

            getActivity().stopService(myIntent);
            getActivity().stopService(myIntent2);

            s1 = false;
            s2 = false;

            count = 0;
        } else if (type_num == 7) {
            SetMainAnimationStop();
            iv_mainAnimation.stop();
            drawable = getResources().getDrawable(R.drawable.img_driveoff);
            btn.setImageDrawable(drawable);
            OnService = false;
            OnSleepService = false;
            println("주행을 종료합니다");

            getActivity().stopService(myIntent);
            getActivity().stopService(myIntent2);
            getActivity().stopService(myIntent3);

            s1 = false;
            s2 = false;
            s3 = false;

            count = 0;
        }
    }

    public static boolean isNetworkState(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobile = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo lte_4g = manager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX);
        boolean blte_4g = false;
        if (lte_4g != null)
            blte_4g = lte_4g.isConnected();
        if (mobile != null) {
            if (mobile.isConnected() || wifi.isConnected() || blte_4g)
                return true;
        } else {
            if (wifi.isConnected() || blte_4g)
                return true;
        }
        return false;
    }


    public void SetMainFlag() {
        spdis_accident = 1000*Integer.parseInt(ndf.sp_accident.getSelectedItem().toString().substring(0, 1));
        spdis_caution = 1000*Integer.parseInt(ndf.sp_caution.getSelectedItem().toString().substring(0, 1));
        sptime_prevention = Integer.parseInt(ndf.sp_prevention_time.getSelectedItem().toString().substring(0, 2));
        ndf.spsound_decibel = Integer.parseInt(ndf.sp_prevention_decibel.getSelectedItem().toString().substring(0, 3));

        onoff_accident = ndf.sw_accident.isChecked();
        onoff_caution = ndf.sw_caution.isChecked();
        onoff_prevention = ndf.sw_prevention.isChecked();
        //onoff_myinfo = ndf.sw_myInfo.isChecked();
        //onoff_cctv = !ndf.sw_cctv.isChecked();
        onoff_myinfo = true;
        onoff_cctv = false;

        network_onoff = isNetworkState(getActivity());
    }

    /**
     * 단말 등록
     */
    private void registerDevice() {

        RegisterThread registerObj = new RegisterThread();
        registerObj.start();
    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }


    private void getInitialAPIToDevice() {
        GetInitialApiThread getInitialApiThread = new GetInitialApiThread();
        getInitialApiThread.start();
    }

    public static void sendSuddenStoppedPoint() {
        SendSuddenStoppedPointThread sendSuddenStoppedPointThread = new SendSuddenStoppedPointThread();
        sendSuddenStoppedPointThread.start();
    }

    class RegisterThread extends Thread {
        public void run() {
            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getActivity().getApplicationContext());//등록하는 과정(간단함) ,라이브러리하고 jar파일 추가
                regId = gcm.register(GCMInfo.PROJECT_ID);//등록아이디를 받음
                Log.d("고유값: ", regId);
                task_insert = new phpInsert();
                task_insert.execute("http://en605.woobi.co.kr/incident_alarm/gcm_regid.php?id=" + regId + "&serialNumber=" + serial_number);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    class GetInitialApiThread extends Thread {
        public GetInitialApiThread() {
        }

        public void run() {
            try {
                getInitialApi();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void getInitialApi() {
            task_initial = new phpInitialApi();
            task_initial.execute("http://en605.woobi.co.kr/incident_alarm/incident_api_init.php?regId=" + regId);
        }

    }

    public static class SendSuddenStoppedPointThread extends Thread {
        public SendSuddenStoppedPointThread() {

        }

        public void run() {
            try {
                sendStoppedPoint(xPoint, yPoint, regId);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        public void sendStoppedPoint(String xPoint, String yPoint, String regId) {
            task_suddenStop = new phpSuddenStop();
            String updownDiv = "S";//하행
            if (updown) {
                updownDiv = "E";//상행
            }
            task_suddenStop.execute("http://en605.woobi.co.kr/incident_alarm/suddenStopAlarm.php?xPoint=" + xPoint + "&yPoint=" + yPoint + "&regId=" + regId + "&updownDiv=" + updownDiv);
        }
    }


    private class phpInsert extends AsyncTask<String, Integer, String> {

        /*
        * phpInsert task_insert;
        * task_insert = new phpInsert();
        * id = idTxt.getText().toString();
        * task_insert.execute("http://en605.woobi.co.kr/incident_alarm/gcmtest_insert.php?id=" + this.id);
        * */

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder resultText = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                        for (; ; ) {
                            // 웹상에 보여지는 텍스트를 라인단위로 읽어 저장.
                            String line = br.readLine();
                            if (line == null) break;
                            // 저장된 텍스트 라인을 jsonHtml에 붙여넣음
                            resultText.append(line);
                        }
                        br.close();
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return resultText.toString();

        }

        protected void onPostExecute(String str) {
            if (str.equals("1")) {
                //Toast.makeText(getApplicationContext(),"푸시 서비스를 위해 단말을 등록했습니다.",Toast.LENGTH_LONG).show();
            } else {
                //Toast.makeText(getApplicationContext(),"푸시 서비스를 위한 단말이 이미 등록되어 있습니다.",Toast.LENGTH_LONG).show();
            }

        }
    }

    private class phpInitialApi extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            StringBuilder resultStr = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //return resultStr.toString();
            return null;

        }

        protected void onPostExecute() {
        }

    }

    public static class phpSuddenStop extends AsyncTask<String, Integer, Void> {

        @Override
        protected Void doInBackground(String... urls) {
            StringBuilder resultStr = new StringBuilder();
            try {
                // 연결 url 설정
                URL url = new URL(urls[0]);
                // 커넥션 객체 생성
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                // 연결되었으면.
                if (conn != null) {
                    conn.setConnectTimeout(10000);
                    conn.setUseCaches(false);
                    // 연결되었음 코드가 리턴되면.
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    }
                    conn.disconnect();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            //return resultStr.toString();
            return null;

        }

        protected void onPostExecute() {
        }


    }

    private void println(String msg) {
        final String output = msg;
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(getActivity().getApplicationContext(), output, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }


}
