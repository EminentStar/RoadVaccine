package br.com.halyson.ensharp.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;

import br.com.halyson.ensharp.R;


public class IntroductionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.introduction);

    }

    public void onCloseBtnClicked(View v){
        finish();
    }
    public void onCloseImgBtnClicked(View v){
        finish();
    }


}