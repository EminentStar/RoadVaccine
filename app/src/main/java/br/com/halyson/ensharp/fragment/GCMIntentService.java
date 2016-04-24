package br.com.halyson.ensharp.fragment;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Jimin on 2015-07-11.
 */
public class GCMIntentService extends IntentService {

    private static final String TAG = "GCMIntentService";

    /**
     * ������
     */
    public GCMIntentService() {
        super(TAG);

        Log.d(TAG, "GCMIntentService() called.");
    }

    /*
     * ���޹��� ����Ʈ ó��
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();

        Log.d(TAG, "action : " + action);

    }

}