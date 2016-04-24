package br.com.halyson.ensharp.fragment;

/**
 * Created by Jimin on 2015-07-11.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import java.net.URLDecoder;

import br.com.halyson.ensharp.activity.EnrollData;
import br.com.halyson.ensharp.activity.EnrollDataCaution;
import br.com.halyson.ensharp.activity.PopupActivity;

/**
 * 푸시 메시지를 받는 Receiver 정의
 *
 * @author Mike
 */
public class GCMBroadcastReceiver extends WakefulBroadcastReceiver {
    private static final String TAG = "GCMBroadcastReceiver";

    Fragment2 ma = new Fragment2();
    EnrollData save = new EnrollData();
    String[] spl;
    boolean isSame = false;

    @Override
    public void onReceive(Context context, Intent intent) {        //상대방이 메시지 보낼때  intent의 부가적인 정보로 사용
        String action = intent.getAction();
        Log.d(TAG, "action : " + action);

        if (action != null) {
            if (action.equals("com.google.android.c2dm.intent.RECEIVE")) { // 푸시 메시지 수신 시
                String from = intent.getStringExtra("from");
                String command = intent.getStringExtra("command");        // 서버에서 보낸 command 라는 키의 value 값
                String type = intent.getStringExtra("type");        // 서버에서 보낸 type 라는 키의 value 값
                String rawData = intent.getStringExtra("data");        // 서버에서 보낸 data 라는 키의 value 값
                String data = "";
                try {
                    data = URLDecoder.decode(rawData, "UTF-8");//다시 디코드...?
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                Log.v(TAG, "from : " + from + ", command : " + command + ", type : " + type + ", data : " + data);

                if (data.equals("")) {
                    return;
                } else if (data.equals("{}")) {
                    return;
                } else if (!data.contains("{") && !data.contains("}")) {
                    String replace_data = ((data.replace("(", "")).replace(")", "")).replace(",", "/");
                    spl = replace_data.split("/");

                    for (int i = 0; i < ma.list_coutiondata.size(); i++) {
                        if (ma.list_coutiondata.get(i).GetKind().equals(spl[2])) {
                            ma.list_coutiondata.remove(i);
                            isSame = true;
                            Log.e(TAG, ma.list_coutiondata.size() + "delete");
                        }
                    }
                    if (!isSame) {
                        ma.list_coutiondata.add(new EnrollDataCaution(spl[2], "주의차량 조심", "1", spl[0], spl[1], spl[3], "yet"));
                        isSame=false;
                        Log.e(TAG, spl[2]);
                        Log.e(TAG, ma.list_coutiondata.size() + "");
                    }
                } else if (data.contains("{") && data.contains("}")) {
                    String replace_data = ((data.replace("{", "")).replace("}", "")).replace(",", "/");

                    spl = replace_data.split("/");

                    int i = 0;
                    int j = 0;
                    for (i = 0; i <= spl.length - 1; i++) {
                        if (spl[i].equals("")) {
                            spl[i] = "0.0";
                        }
                    }

                    for (i = 0; i <= spl.length - 1; i += 6) {
                        if (Exist_list(spl[i + 2], spl[i + 3], spl[i + 4])) {
                            if (!spl[i + 3].equals("0.0")) {
                                if (spl[i + 1].contains("m")) {
                                    spl[i + 1] = spl[i + 1].replace("m", "m ");
                                }
                                if (spl[i].equals("00")) {
                                    ma.list_data.add(new EnrollData(spl[i], "사고발생", spl[i + 2], spl[i + 3], spl[i + 4], spl[i + 5], "yet"));
                                } else {
                                    ma.list_data.add(new EnrollData(spl[i], "공사중", spl[i + 2], spl[i + 3], spl[i + 4], spl[i + 5], "yet"));
                                }
                                Log.e(TAG, ma.list_data.size() + "");
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Unknown action : " + action);
            }
        } else {
            Log.d(TAG, "action is null.");
        }

    }

    public boolean Exist_list(String value1, String value2, String value3) //시간 위도 경도
    {
        if (ma.list_data.size() == 0)
            return true;
        else {
            int i = 0;
            for (i = 0; i <= ma.list_data.size() - 1; i++) {
                if (value2.equals(ma.list_data.get(i).GetLat()) && value3.equals(ma.list_data.get(i).GetLon())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 메인 액티비티로 수신된 푸시 메시지의 데이터 전달
     *
     * @param context
     * @param command
     * @param type
     * @param data
     */
    private void sendToActivity(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, PopupActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }

}