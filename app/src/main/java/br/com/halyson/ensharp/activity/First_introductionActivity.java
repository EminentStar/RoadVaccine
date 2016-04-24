package br.com.halyson.ensharp.activity;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.halyson.ensharp.R;


public class First_introductionActivity extends Activity {

    CheckBox cb_close;
    ImageButton btn;
    Button closeBtn;
    Typeface myTypeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.first_introduction);
        myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");
        cb_close = (CheckBox) findViewById(R.id.fragment_first_instruction_checkbox);
        cb_close.setTypeface(myTypeface);
        btn = (ImageButton) findViewById(R.id.fragment_first_instruction_imageButton);
        closeBtn = (Button) findViewById(R.id.closeBtn);

        cb_close.bringToFront();
        closeBtn.bringToFront();
    }

    public void onCloseBtnClicked(View v) {
        HomeActivity.home_editor.putBoolean("key", cb_close.isChecked());
        HomeActivity.home_editor.commit();
        finish();
    }

    public void onCloseImgBtnClicked(View v) {
        HomeActivity.home_editor.putBoolean("key", cb_close.isChecked());
        HomeActivity.home_editor.commit();
        finish();
    }

}