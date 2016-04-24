package br.com.halyson.ensharp.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import br.com.halyson.ensharp.R;


public class Ins4Activity extends Activity {

    TextView tv_title;
    Typeface myTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.ins4_activity);

        myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");
        tv_title= (TextView) findViewById(R.id.tv_title);
        tv_title.setTypeface(myTypeface);

    }

    private void println(String msg) {
        final String output = msg;
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
    }


    public void btn_xOnclicked(View v) {
        finish();
    }
}
