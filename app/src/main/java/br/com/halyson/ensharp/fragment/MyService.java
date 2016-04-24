package br.com.halyson.ensharp.fragment;

/**
 * Created by Jimin on 2015-07-12.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import br.com.halyson.ensharp.activity.CctvActivity;
import br.com.halyson.ensharp.activity.EnrollData;
import br.com.halyson.ensharp.activity.EnrollDataCaution;
import br.com.halyson.ensharp.activity.LockCctvActivity;
import br.com.halyson.ensharp.activity.LockPopup2Activity;
import br.com.halyson.ensharp.activity.LockPopupActivity;
import br.com.halyson.ensharp.activity.Popup2Activity;
import br.com.halyson.ensharp.activity.PopupActivity;
import br.com.halyson.ensharp.activity.SecondActivity;

public class MyService extends Service implements Runnable {

    Fragment2 maa = new Fragment2();
    NavigationDrawerFragment ndf = new NavigationDrawerFragment();
    OnService os = new OnService();
    UserException ue = new UserException();

    Double distance = 0.0;
    boolean service_on = false;
    Thread myThread;

    Double acc_distance = 0.0;
    Double my_distance = 0.0;


    /**
     * 디버깅을 위한 태그
     */
    public static final String TAG = "MyService";

    /**
     * 반복 횟수
     */
    private int count = 0;

    /**
     * 서비스 객체 생성 시 자동 호출됩니다.
     */
    public void onCreate() {
        super.onCreate();
        service_on = true;

        // 스레드를 이용해 반복하여 로그를 출력합니다.
        myThread = new Thread(this);
        myThread.start();
    }

    /**
     * 스레드의 실행 부분
     */
    public void run() {
        while (service_on) {
            try {
                if (myThread.currentThread().isInterrupted()) {
                    myThread.interrupt();
                }
                //Log.i(TAG, "my service called #" + count);
                //count++;
                SetFlag();

                int i = 0;

                if (maa.list_data.size() != 0) {
                    if (maa.onoff_accident == true) {
                        for (i = 0; i < maa.list_data.size(); i++) {
                            SetFlag();
                            if (maa.list_data.get(i).GetUsed_flag().equals("yet")) {

                                distance = calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.list_data.get(i).GetLat()), Double.parseDouble(maa.list_data.get(i).GetLon()));

                                acc_distance = calDistance(37.566656, 126.978428, Double.parseDouble(maa.list_data.get(i).GetLat()), Double.parseDouble(maa.list_data.get(i).GetLon()));//시청-사고 거리
                                my_distance = calDistance(37.566656, 126.978428, maa.current_lat, maa.current_lon);//시청-내 거리

                                if (maa.updown == true && distance <= maa.spdis_accident)//상행중
                                {
                                    if (maa.list_data.get(i).GetUpdown().equals("E")) {
                                        if (acc_distance > my_distance) {
                                            //지나침, 팝업x
                                        } else if (acc_distance < my_distance && ue.OnPopup == false && ue.CCTVOnPopup == false && ue.SleepOnPopup == false && maa.onoff_accident == true) {
                                            maa.currentacc_lat = Double.parseDouble(maa.list_data.get(i).GetLat());
                                            maa.currentacc_lon = Double.parseDouble(maa.list_data.get(i).GetLon());
                                            if (maa.list_data.get(i).GetKind().equals("00")) {
                                                maa.isAccident = true;
                                                maa.isCaution = false;
                                            } else if (maa.list_data.get(i).GetKind().equals("03")) {
                                                maa.isAccident = false;
                                                maa.isCaution = false;
                                            }
                                            if (maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToActivity(this, "", "", "", maa.list_data.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), CctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            } else if (!maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToLockActivity(this, "", "", "", maa.list_data.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), LockCctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            }
                                            maa.list_data.set(i, new EnrollData("1", "1", "1", maa.list_data.get(i).GetLat(), maa.list_data.get(i).GetLon(), "1", "used"));
                                            ue.OnPopup = true;
                                            Thread.sleep(7000);

                                            SetFlag();
                                        }
                                    }
                                } else if (maa.updown == false && distance <= maa.spdis_accident)//하행중
                                {
                                    if (maa.list_data.get(i).GetUpdown().equals("S")) {
                                        if (acc_distance > my_distance && ue.OnPopup == false && ue.CCTVOnPopup == false && ue.SleepOnPopup == false && maa.onoff_accident == true) {
                                            maa.currentacc_lat = Double.parseDouble(maa.list_data.get(i).GetLat());
                                            maa.currentacc_lon = Double.parseDouble(maa.list_data.get(i).GetLon());
                                            if (maa.list_data.get(i).GetKind().equals("00")) {
                                                maa.isAccident = true;
                                                maa.isCaution = false;
                                            } else if (maa.list_data.get(i).GetKind().equals("03")) {
                                                maa.isAccident = false;
                                                maa.isCaution = false;
                                            }
                                            if (maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToActivity(this, "", "", "", maa.list_data.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), CctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            } else if (!maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToLockActivity(this, "", "", "", maa.list_data.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), LockCctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            }
                                            maa.list_data.set(i, new EnrollData("1", "1", "1", maa.list_data.get(i).GetLat(), maa.list_data.get(i).GetLon(), "1", "used"));
                                            ue.OnPopup = true;
                                            Thread.sleep(7000);

                                            SetFlag();
                                        } else if (acc_distance < my_distance) {
                                            //지나침, 팝업x
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if (maa.list_coutiondata.size() != 0) {
                    if (maa.onoff_caution == true) {
                        for (i = 0; i < maa.list_coutiondata.size(); i++) {
                            SetFlag();
                            if (maa.list_coutiondata.get(i).GetUsed_flag().equals("yet")) {

                                distance = calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.list_coutiondata.get(i).GetLat()), Double.parseDouble(maa.list_coutiondata.get(i).GetLon()));

                                acc_distance = calDistance(37.566656, 126.978428, Double.parseDouble(maa.list_coutiondata.get(i).GetLat()), Double.parseDouble(maa.list_coutiondata.get(i).GetLon()));//시청-사고 거리
                                my_distance = calDistance(37.566656, 126.978428, maa.current_lat, maa.current_lon);//시청-내 거리

                                if (maa.updown == true && distance <= maa.spdis_caution)//상행중
                                {
                                    if (maa.list_coutiondata.get(i).GetUpdown().equals("E")) {
                                        if (acc_distance > my_distance) {
                                            //지나침, 팝업x
                                        } else if (acc_distance < my_distance && ue.OnPopup == false && ue.CCTVOnPopup == false && ue.SleepOnPopup == false && maa.onoff_caution == true) {
                                            maa.currentacc_lat = Double.parseDouble(maa.list_coutiondata.get(i).GetLat());
                                            maa.currentacc_lon = Double.parseDouble(maa.list_coutiondata.get(i).GetLon());
                                            maa.isCaution = true;
                                            if (maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToActivity2(this, "", "", "", maa.list_coutiondata.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), CctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            } else if (!maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToLockActivity2(this, "", "", "", maa.list_coutiondata.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), LockCctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            }
                                            maa.list_coutiondata.set(i, new EnrollDataCaution("1", "1", "1", maa.list_coutiondata.get(i).GetLat(), maa.list_coutiondata.get(i).GetLon(), "1", "used"));
                                            ue.OnPopup = true;
                                            Thread.sleep(7000);

                                            SetFlag();
                                        }
                                    }
                                } else if (maa.updown == false && distance <= maa.spdis_caution)//하행중
                                {
                                    if (maa.list_coutiondata.get(i).GetUpdown().equals("S")) {
                                        if (acc_distance > my_distance && ue.OnPopup == false && ue.CCTVOnPopup == false && ue.SleepOnPopup == false && maa.onoff_caution == true) {
                                            maa.currentacc_lat = Double.parseDouble(maa.list_coutiondata.get(i).GetLat());
                                            maa.currentacc_lon = Double.parseDouble(maa.list_coutiondata.get(i).GetLon());
                                            maa.isCaution = true;
                                            if (maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToActivity2(this, "", "", "", maa.list_coutiondata.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), CctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            } else if (!maa.isScreenOn(this)) {
                                                if (maa.onoff_cctv == false) {
                                                    sendToLockActivity2(this, "", "", "", maa.list_coutiondata.get(i).GetContent());
                                                } else if (maa.onoff_cctv == true) {
                                                    Intent intent_cctv = new Intent(getApplicationContext(), LockCctvActivity.class);
                                                    intent_cctv.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    startActivity(intent_cctv);
                                                    ue.CCTVOnPopup = true;
                                                }
                                            }
                                            maa.list_coutiondata.set(i, new EnrollDataCaution("1", "1", "1", maa.list_coutiondata.get(i).GetLat(), maa.list_coutiondata.get(i).GetLon(), "1", "used"));
                                            ue.OnPopup = true;
                                            Thread.sleep(7000);

                                            SetFlag();
                                        } else if (acc_distance < my_distance) {
                                            //지나침, 팝업x
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, ex.toString());
            }
        }

    }

    public void SetFlag() {
        maa.spdis_accident = 1000*Integer.parseInt(ndf.sp_accident.getSelectedItem().toString().substring(0, 1));
        maa.spdis_caution = 1000*Integer.parseInt(ndf.sp_caution.getSelectedItem().toString().substring(0, 1));
        maa.sptime_prevention = Integer.parseInt(ndf.sp_prevention_time.getSelectedItem().toString().substring(0, 2));
        ndf.spsound_decibel = Integer.parseInt(ndf.sp_prevention_decibel.getSelectedItem().toString().substring(0, 3));

        maa.onoff_accident = ndf.sw_accident.isChecked();
        maa.onoff_caution = ndf.sw_caution.isChecked();
        maa.onoff_prevention = ndf.sw_prevention.isChecked();
        //maa.onoff_myinfo = ndf.sw_myInfo.isChecked();
        //maa.onoff_cctv = !ndf.sw_cctv.isChecked();
        maa.onoff_myinfo = true;
        maa.onoff_cctv = false;
    }

    public static boolean isScreenOn(Context context) {
        return ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).isScreenOn();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (myThread != null && myThread.isAlive()) {
            myThread.interrupt();
        }
        count = 0;
        service_on = false;
        stopSelf();
    }

    private void sendToActivity(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, PopupActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    private void sendToLockActivity(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, LockPopupActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    private void sendToActivity2(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, Popup2Activity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    private void sendToLockActivity2(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, LockPopup2Activity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

    public double calDistance(double lat1, double lon1, double lat2, double lon2) {

        Double theta, dist;
        theta = lon1 - lon2;
        dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);

        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        dist = dist * 1000.0;

        return dist;
    }

    private double deg2rad(double deg) {
        return (Double) (deg * Math.PI / (Double) 180d);
    }

    private double rad2deg(double rad) {
        return (Double) (rad * (Double) 180d / Math.PI);
    }


    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

}
