package br.com.halyson.ensharp.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.activity.api.BaseActivity;
import br.com.halyson.ensharp.constants.FragmentNames;
import br.com.halyson.ensharp.fragment.Fragment2;
import br.com.halyson.ensharp.fragment.HomeFragment;
import br.com.halyson.ensharp.fragment.NavigationDrawerFragment;
import br.com.halyson.ensharp.fragment.UserException;


public class HomeActivity extends BaseActivity {

    NavigationDrawerFragment ndf = new NavigationDrawerFragment();
    Fragment2 maa = new Fragment2();
    UserException ue = new UserException();


    public static SharedPreferences home_sp;
    public static SharedPreferences.Editor home_editor;

    public static boolean home_checkIns;

    String strUrl;

    public static ImageButton btn_q;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        

        strUrl = "http://openapi.its.go.kr/api/NCCTVInfo?key=1436770430812&ReqType=2&MinX=127.100000&MaxX=128.890000&MinY=34.100000&MaxY=39.100000";
        home_checkIns = false;
        home_sp = PreferenceManager.getDefaultSharedPreferences(this);
        home_editor = home_sp.edit();
        home_checkIns = home_sp.getBoolean("key", false);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.screen_default_container, new HomeFragment(), FragmentNames.FRAGMENT_HOME_).commit();
            if (home_checkIns == false) {
                startActivity(new Intent(getApplicationContext(), First_introductionActivity.class));
            }
            startActivity(new Intent(this, SplashActivity.class));
        }
    }

    public void onButtonsettingClicked(View v) {
        ndf.OpenIt();
    }


    public boolean isOnGps() {
        maa.manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!maa.manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            return false;
        }
        return true;
    }

    private void println(String msg) {
        final String output = msg;
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
    }

    @Override
    protected int setLayoutResourceIdentifier() {
        return R.layout.screen_default;
    }

    @Override
    protected int getTitleToolBar() {
        return R.string.app_name;
    }

}