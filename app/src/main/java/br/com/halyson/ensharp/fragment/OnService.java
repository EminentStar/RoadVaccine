package br.com.halyson.ensharp.fragment;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import br.com.halyson.ensharp.activity.CctvActivity;
import br.com.halyson.ensharp.activity.LockCctvActivity;
import br.com.halyson.ensharp.activity.LockPopup2Activity;
import br.com.halyson.ensharp.activity.MyCctvActivity;
import br.com.halyson.ensharp.activity.Popup2Activity;
import br.com.halyson.ensharp.activity.PopupActivity;

/**
 * Created by Jimin on 2015-07-21.
 */
public class OnService extends Service {
    boolean count = false;
    int UpDownCount;
    Handler handler;

    Fragment2 maa = new Fragment2();
    NavigationDrawerFragment ndf = new NavigationDrawerFragment();

    private static final String TAG = "OnService";

    LocationManager manager;
    MyLocationListener[] listener = new MyLocationListener[]{
            new MyLocationListener(),
            new MyLocationListener()
    };


    boolean gps_enabled = false;
    boolean network_enabled = false;

    UserException userException;

    // ********************** Two GPS ************************* //
    Switch sw_accident, sw_construction, sw_caution;

    Double destinationLatitude, destinationLongitude, tempLatitude, tempLongitude;
    Double currentLatitude, currentLongitude;
    Double currentDistance, beforeDistance, formerDistance, firstDistance;

    boolean passCheck, apiCheck, approachCheck;
    long passStartTime, afterPassTime, desElapsedTime;

    // ************************ Sudden Stop ***************************** //
    Switch sw_push;

    float maxSpeed, currentSpeed, beforeSpeed, minSpeed;
    int currentCount, minCount;

    long suddenElapsedTime, suddenStopStartTime, currentTime;
    boolean suddenStopStartCheck, suddenStopCheck;

    Double gapDistance, beforeLatitude, beforeLongitude;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");

        userException = new UserException();

        // ****************** Two GPS ********************** //

        destinationLatitude = destinationLongitude = tempLatitude = tempLongitude = 0.0;
        currentLatitude = currentLongitude = currentDistance = beforeDistance = formerDistance = 0.0;

        passCheck = apiCheck = approachCheck = false;
        passStartTime = afterPassTime = desElapsedTime = 0;
        UpDownCount = 0;

        // ************************* Sudden Stop ************************ //

        maxSpeed = currentSpeed = beforeSpeed = minSpeed = 0;
        minSpeed = 49; // ��ӵ��δ� �����ӵ� 50km/h

        currentCount = minCount = 0;

        suddenElapsedTime = suddenStopStartTime = currentTime = 0;
        suddenStopStartCheck = suddenStopCheck = false;

        gapDistance = beforeLatitude = beforeLongitude = firstDistance = 0.0;


        // ******************** Manual Push ************************* //

        // ******************** Activity ******************* //
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        long minTime = 1000;
        float minDistance = 0;

        //listener[0] = new MyLocationListener();
        //listener[1] = new MyLocationListener();

        gps_enabled = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        network_enabled = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        /*if (!gps_enabled && !network_enabled) {
            Log.e(TAG, "!gps_enabled !network_enabled");
        } else {
            if (network_enabled) {
                Log.e(TAG, "network_enabled");
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener[0]);
            }
            if (gps_enabled) {
                Log.e(TAG, "gps_enabled");
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener[1]);
            }
        }*/
        try {
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, listener[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, listener[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
    }


    public void SuddenStopCheck() {
        // ***************************** Sudden Stop Check ********************************* //
        if (true) { // ���� ������ ��Ȳ�� push�ҰŶ� �����ߴٸ�.

            if (currentSpeed != 0 && currentSpeed > beforeSpeed) { // �ӵ��� ����.

                // ����ӵ��� 0�� �ƴϰ� ������ �ӵ��� ������ maxSpeed�� ����. ��, ���������Ҷ��� �ӵ��� ���ϱ� ����.
                maxSpeed = currentSpeed;
                minCount = userException.getMinCount(maxSpeed); // ����ӵ��� �´� minCount�� ������.

                // �ӵ��� �����ϸ� �������ʹ� �ݴ밳���̹Ƿ� �������� ���̴� �������� �ʱ�ȭ.
                suddenStopCheck = suddenStopStartCheck = false;
                gapDistance = 0.0;
                suddenElapsedTime = currentTime = suddenStopStartTime = 0;
                currentCount = 0;
                count = false;
            } else { // �ӵ� ����.

                // ���������� GPS�� �ϳ��� ��ǥ�� �ƴ϶� ��� ���ϴµ�, �� ���ϴ� ���� ������ �Ÿ��� ���� 1m�̸���.
                gapDistance = userException.calDistance(beforeLatitude, beforeLongitude, currentLatitude, currentLongitude);

                // ������� ������ ������ �ð��� ��. ��, �������ϰ� ������ �ִ� �ð��� ����.
                if (suddenStopStartCheck == true) {
                    currentTime = System.currentTimeMillis() / 1000;
                    suddenElapsedTime = currentTime - suddenStopStartTime;
                }

                if (suddenStopCheck == true) { // �������� �� ���¶��.
                    // push
                } else {
                    if (maxSpeed >= minSpeed) { // ��ӵ��� �����ӵ����� ���� �ӵ��� �޸��ٰ� �����ϴ� ���.
                        currentCount++;

                        if (currentCount <= minCount) { // maxSpeed�� �´� minCount���� ���� Count�� ���. �� �������� ����.

                            // �������� �Ϸ�� ����. ��, ������ �����Ǵ� �ð��� ���ϱ� ����.
                            if (gapDistance <= 10 && beforeSpeed <= 1 && currentSpeed <= 1 && suddenStopStartCheck == false) {
                                suddenStopStartCheck = true;
                                suddenStopStartTime = System.currentTimeMillis() / 1000;

                                //Toast.makeText(getApplicationContext(), "Start SuddenStop", Toast.LENGTH_LONG).show();
                                //sendToActivity(this, "", "", "", "Start SuddenStop");
                            }
                        } else { // ��� �����ϰ� �ִ� ���.
                            // ������ �Ϸ� ��, �ӵ��� �������� �ʴ� ���.
                            if (suddenStopStartCheck == true) {
                                if (gapDistance <= 10 && beforeSpeed <= 1 && currentSpeed <= 1 && suddenElapsedTime >= 5) { // ��� �������¶��
                                    // �������°� 5�� �̻� �Ǿ��ٸ� �˸��� ������.
                                    //Toast.makeText(getApplicationContext(), "Sudden Stop", Toast.LENGTH_LONG).show();
                                    //sendToActivity(this, "", "", "", "Sudden Stop");

                                    if (maa.onoff_myinfo == true && count == false) {
                                        maa.xPoint = String.valueOf(maa.current_lon);
                                        maa.yPoint = String.valueOf(maa.current_lat);
                                        maa.sendSuddenStoppedPoint();//위치 자동전송
                                        maa.isAgain = true;
                                        count = true;
                                    }
                                }
                            } // if( suddenStopStartCheck == true )
                        }
                    }
                }
            } // Speed Down
        } // Push On
    }

    public void CheckUpDown() {
        // �����û ��ǥ
        destinationLatitude = 37.566656;
        destinationLongitude = 126.978428;

        if (UpDownCount == 10)
            firstDistance = currentDistance;
        else if (UpDownCount == 70) beforeDistance = currentDistance;
        else if (UpDownCount == 130) {
            formerDistance = currentDistance;
            if (formerDistance > beforeDistance && beforeDistance > firstDistance) {//하행
                maa.updown = false;
            }
            if (formerDistance < beforeDistance && beforeDistance < firstDistance) {//상행
                maa.updown = true;
            }
            UpDownCount = 0;
        }


        // ��ǥ���������� �Ÿ��� ����.
        // (������ �����Ÿ� > ������ ���Ÿ�) && (������ ���Ÿ� > ����Ÿ�) && ��ǥ������ ��������� ���.

    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            Log.e(TAG, "onLocationChanged: " + currentLatitude + ", " + currentLongitude);

            UpDownCount++;
            currentDistance = userException.calDistance(destinationLatitude, destinationLongitude, currentLatitude, currentLongitude);
            CheckUpDown();//상하행 체크

            SetFlag();

            beforeLatitude = currentLatitude; // suddenStop GapDistance���ϱ� ���� ���.
            beforeLongitude = currentLongitude;

            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

            maa.current_lat = currentLatitude;
            maa.current_lon = currentLongitude;

            beforeSpeed = currentSpeed;
            currentSpeed = location.getSpeed();
            currentSpeed = userException.msTokh(currentSpeed);

            if (maa.isAgain && currentSpeed > 30.0) {
                maa.sendSuddenStoppedPoint();//위치 자동전송
                maa.isAgain = false;
            }

            SuddenStopCheck();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(getApplicationContext(), "onStatus", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(getApplicationContext(), "onProvider", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(getApplicationContext(), "Disabled", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

        if (manager != null) {
            for (int i = 0; i < listener.length; i++) {
                try {
                    manager.removeUpdates(listener[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
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

    public void SetFlag() {
        maa.spdis_accident = 1000 * Integer.parseInt(ndf.sp_accident.getSelectedItem().toString().substring(0, 1));
        maa.spdis_caution = 1000 * Integer.parseInt(ndf.sp_caution.getSelectedItem().toString().substring(0, 1));
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

    //������ �Ǻ� �޼ҵ�
    //ma.updown=;
}