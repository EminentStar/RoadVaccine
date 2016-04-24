package br.com.halyson.ensharp.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.ImageView;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;

import br.com.halyson.ensharp.R;


public class SplashActivity extends Activity {

    Drawable drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        ImageView iv_splash=(ImageView) findViewById(R.id.iv_splash);
        iv_splash.setBackgroundResource(R.drawable.iv_splash_animation);
        AnimationDrawable iv_splashAnimation = (AnimationDrawable) iv_splash.getBackground();
        iv_splashAnimation.start();

        Handler hd = new Handler();
        hd.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1800);

    }
}