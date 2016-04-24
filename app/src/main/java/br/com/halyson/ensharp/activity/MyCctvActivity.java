package br.com.halyson.ensharp.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.halyson.ensharp.R;
import br.com.halyson.ensharp.fragment.Fragment2;
import br.com.halyson.ensharp.fragment.UserException;


public class MyCctvActivity extends Activity {

    VideoView video;
    Fragment2 maa=new Fragment2();
    UserException ue=new UserException();

    public static TextView tv_distance;

    MediaController mc;

    int i=0;
    int min_index=0;
    int fir_index=0;

    public static Double min_dis=0.0;
    public static Double cctvtohere=0.0;

    public static Double cctv_lat=0.0;
    public static Double cctv_lon=0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mycctv_activity);

        maa.cctv_data.clear();
        callapi();

        mc = new MediaController(this);
        Handler autohandler = new Handler() {
            public void handleMessage(Message msg) {
                if (maa.cctv_data.size() != 0) {

                    min_dis = calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.cctv_data.get(0).GetCctv_lat()), Double.parseDouble(maa.cctv_data.get(0).GetCctv_lon()));
                    for (i = 1; i < maa.cctv_data.size()-1; i++) {
                        if (min_dis >= calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.cctv_data.get(i).GetCctv_lat()), Double.parseDouble(maa.cctv_data.get(i).GetCctv_lon()))) {
                            min_dis = calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.cctv_data.get(i).GetCctv_lat()), Double.parseDouble(maa.cctv_data.get(i).GetCctv_lon()));
                            min_index = i;
                            fir_index = min_index;
                        }
                    }


                    //MediaController mc = new MediaController(getApplication());
                    //mc = new MediaController(this);
                    mc.setAnchorView(video);


                    video.setMediaController(null);
                    video.setVideoURI(Uri.parse(maa.cctv_data.get(min_index).GetCctv_uri()));
                    video.requestFocus();

                    video.start();
                    //tv_count.setText(maa.cctv_data.get(10).GetCctv_lat()+"");
                    cctvtohere=min_dis;

                    cctv_lat=Double.parseDouble(maa.cctv_data.get(min_index).GetCctv_lat());
                    cctv_lon=Double.parseDouble(maa.cctv_data.get(min_index).GetCctv_lon());

                } else {
                    println("네트워크가 불안정하므로\n다시 실행해주세요");
                }

                Thread thread = new Thread(){
                    public void run(){
                        while(true){
                            try{
                                sleep(1000);
                            }catch (Exception e) {
                                // TODO: handle exception
                                e.printStackTrace();
                            }
                            handler.sendEmptyMessage(0);
                        }
                    }
                };
                thread.start();
            }
        };
        autohandler.sendEmptyMessageDelayed(0, 3000);

        Typeface myTypeface = Typeface.createFromAsset(getAssets(), "addi.ttf");

        tv_distance = (TextView) findViewById(R.id.tv_distance);
        tv_distance.setText("");
        tv_distance.setTypeface(myTypeface);

        ue.CCTVOnPopup = true;

        video = (VideoView) findViewById(R.id.vv_cctv);

    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Double cctvtome = calDistance(cctv_lat, cctv_lon, maa.current_lat, maa.current_lon);
            if (cctvtome <= 0.0 || cctvtome >= 500000.0) {
                tv_distance.setText("");
            } else if (cctvtome < 1000.0) {
                tv_distance.setText(String.format("%.0f", cctvtome) + "m");
            } else {
                tv_distance.setText(String.format("%.1f", cctvtome / 1000.0) + "km");
            }
        }

    };

    private void println(String msg) {
        final String output = msg;
        Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
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

    public void onButtonBefClicked(View v) {
        if(fir_index!=0) {
            fir_index=fir_index-1;
            video.setMediaController(null);
            video.setVideoURI(Uri.parse(maa.cctv_data.get(fir_index).GetCctv_uri()));
            video.requestFocus();

            video.start();

            cctvtohere=calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lat()), Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lon()));

            //tv_distance.setText("CCTV까지의 거리는 : " + String.format("%.1f", cctvtohere/1000.0) + " km 입니다.");
            cctv_lat=Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lat());
            cctv_lon=Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lon());
        }
    }

    public void onButtonAftClicked(View v) {
        if(fir_index!=maa.cctv_data.size()-1) {
            fir_index=fir_index+1;
            video.setMediaController(null);
            video.setVideoURI(Uri.parse(maa.cctv_data.get(fir_index).GetCctv_uri()));
            video.requestFocus();

            video.start();

            cctvtohere=calDistance(maa.current_lat, maa.current_lon, Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lat()), Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lon()));

            //tv_distance.setText("CCTV까지의 거리는 : " + String.format("%.1f",cctvtohere/1000.0) + " km 입니다.");
            cctv_lat=Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lat());
            cctv_lon=Double.parseDouble(maa.cctv_data.get(fir_index).GetCctv_lon());
        }
    }

    public void onButtonCloseClicked(View v) {
        ue.OnPopup=false;
        ue.CCTVOnPopup=false;
        finish();
    }

    public void callapi() {
        try {
            String strUrl = "http://openapi.its.go.kr/api/NCCTVInfo?key=1436770430812&ReqType=2&MinX=127.100000&MaxX=128.890000&MinY=34.100000&MaxY=39.100000";
            //maa.cctv_data.clear();
            if (strUrl != null && strUrl.length() > 0) {
                ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnected()) {
                    new DownloadWebpageText().execute(strUrl);        // html 다운로드 쓰레드 기동

                } else {
                    throw new Exception();
                }
            } else {
                throw new Exception();
            }

        } catch (Exception e) {
        }

    }

    private class DownloadWebpageText extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                return (String) downloadUrl((String) arg0[0]);
            } catch (IOException e) {
                return "download failed";
            }
        }

        protected void onPostExecute(String result) {
            //resultUri = Uri.parse(result);
        }

        private String downloadUrl(String strUrl) throws IOException {
            InputStream is = null;
            int len = 500;
            String returnStr = "";

            try {
                URL url = new URL(strUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(5000);
                conn.setConnectTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                //  int resp = conn.getResponseCode();
                //Log.d(DEBUG_TAG, "The response is: " +resp);
                is = conn.getInputStream();
                try {
                    XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();

                    XmlPullParser parser = parserCreator.newPullParser(); // XMLPullParser 사용
                    parser.setInput(is, null);
                    int parserEvent = parser.getEventType();  // 파싱할 데이터의 타입을 알려준다.
                    String tag;
                    boolean inTitle = false, inTitle2 = false, inTitle3 = false;
                    while (parserEvent != XmlPullParser.END_DOCUMENT) { // xml 파일의 문서 끝인가?
                        switch (parserEvent) {
                            case XmlPullParser.START_DOCUMENT:
                                break;
                            case XmlPullParser.TEXT:
                                tag = parser.getName();
                                if (inTitle) {
                                    String max = parser.getText();
                                    maa.cctv_uri = max;
                                } else if (inTitle2) {
                                    String max2 = parser.getText();
                                    maa.cctv_lat = max2;
                                } else if (inTitle3) {
                                    String max3 = parser.getText();
                                    maa.cctv_lon = max3;
                                }


                                break;

                            case XmlPullParser.END_TAG: // 나중에
                                tag = parser.getName();
                                if (tag.compareTo("cctvurl") == 0) {
                                    inTitle = false;
                                } else if (tag.compareTo("coordy") == 0) {
                                    inTitle2 = false;
                                } else if (tag.compareTo("coordx") == 0) {
                                    inTitle3 = false;
                                    maa.cctv_data.add(new EnrollCCTV(maa.cctv_uri, maa.cctv_lat, maa.cctv_lon));


                                }


                                break;

                            case XmlPullParser.START_TAG: // 먼저
                                tag = parser.getName();

                                if (tag.compareTo("cctvurl") == 0) {
                                    inTitle = true;
                                } else if (tag.compareTo("coordy") == 0) {
                                    inTitle2 = true;
                                } else if (tag.compareTo("coordx") == 0) {
                                    inTitle3 = true;
                                }
                                break;
                            default:
                                break;
                        }
                        parserEvent = parser.next();

                    }
                    //return api_data.get(0);
                } catch (Exception e) {
                    Log.e("dd", "Error in network call", e);
                }
            } finally {
                if (is != null) {
                    Log.e("CctvActivity api ", maa.cctv_data.size() + "");
                    is.close();
                }
            }
            return returnStr;
        }
    }
}
