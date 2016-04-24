package br.com.halyson.ensharp.fragment;

/**
 * Created by Jimin on 2015-07-21.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import br.com.halyson.ensharp.activity.PopupActivity;

public class GPSBroadcastReceiver extends BroadcastReceiver {

    MyService mss=new MyService();
    Fragment2 ma=new Fragment2();

    long minTime = 1000;
    float minDistance = 0;
    double save_lat,save_lon;

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();

        if(action.equals("arabiannight.tistory.com.sendreciver.gogogo")){
            sendToActivity(context, "", "", "", String.valueOf(ma.current_lat + "/" + String.valueOf(ma.current_lon))); //화면으로 전달
        }

    }


    private void sendToActivity(Context context, String from, String command, String type, String data) {
        Intent intent = new Intent(context, PopupActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("command", command);
        intent.putExtra("type", type);
        intent.putExtra("data", data);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_SINGLE_TOP);

        context.startActivity(intent);
    }
}
