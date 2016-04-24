package br.com.halyson.ensharp.fragment;

/**
 * Created by Jimin on 2015-07-12.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import br.com.halyson.ensharp.activity.LockSleepActivity;
import br.com.halyson.ensharp.activity.SecondActivity;
import br.com.halyson.ensharp.activity.SleepActivity;

public class SleepService extends Service implements Runnable {
    Fragment2 maa = new Fragment2();
    NavigationDrawerFragment ndf = new NavigationDrawerFragment();
    UserException ue = new UserException();
    Thread myThread;

    /**
     * 디버깅을 위한 태그
     */
    public static final String TAG = "SleepService";

    /**
     * 반복 횟수
     */

    private int sleep_count = 0;

    /**
     * 서비스 객체 생성 시 자동 호출됩니다.
     */
    public void onCreate() {
        super.onCreate();
        maa.OnSleepService = true;
        // 스레드를 이용해 반복하여 로그를 출력합니다.
        myThread = new Thread(this);
        myThread.start();
    }

    /**
     * 스레드의 실행 부분
     */
    public void run() {
        while (maa.OnSleepService == true && maa.onoff_prevention == true) {
            try {
                if (myThread.currentThread().isInterrupted()) {
                    myThread.interrupt();
                }
                Log.i(TAG, "sleep_count: #" + sleep_count);
                sleep_count++;

                if (sleep_count % (maa.sptime_prevention*60) == 0) {
                    ue.SleepOnPopup = true;
                    if (maa.isScreenOn(this)) {
                        sendToSleepActivity(this); //화면으로 전달
                    } else if (!maa.isScreenOn(this)) {
                        sendToLockSleepActivity(this);
                        //sendToSecondSleepActivity(this);
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
        }

    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (myThread != null && myThread.isAlive()) {
            myThread.interrupt();
        }
        maa.OnSleepService = false;
        sleep_count = 0;
        stopSelf();
    }

    private void sendToSleepActivity(Context context) {
        Intent intent = new Intent(context, SleepActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    private void sendToLockSleepActivity(Context context) {
        Intent intent = new Intent(context, LockSleepActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }
    private void sendToSecondSleepActivity(Context context) {
        Intent intent = new Intent(context, SecondActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
