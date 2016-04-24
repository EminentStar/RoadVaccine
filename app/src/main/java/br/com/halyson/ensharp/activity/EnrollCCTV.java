package br.com.halyson.ensharp.activity;

/**
 * Created by Jimin on 2015-07-19.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class EnrollCCTV implements Parcelable {

    private String cctv_uri; //동영상 주소
    private String cctv_lat; //위도
    private String cctv_lon; //경도

    public EnrollCCTV()
    {
    }

    public EnrollCCTV(String cctv_uri, String cctv_lat, String cctv_lon)
    {
        super();
        this.cctv_uri=cctv_uri;
        this.cctv_lat=cctv_lat;
        this.cctv_lon=cctv_lon;
    }

    public EnrollCCTV(Parcel in){
        readFromParcel(in);
    }

    public String getcctv_uri(){
        return cctv_uri;
    }
    public void setcctv_uri(String cctv_uri){
        this.cctv_uri=cctv_uri;
    }
    public String getcctv_lat(){
        return cctv_lat;
    }
    public void setcctv_lat(String cctv_lat){
        this.cctv_lat=cctv_lat;
    }
    public String getcctv_lon(){
        return cctv_lon;
    }
    public void setcctv_lon(String cctv_lon){
        this.cctv_lon=cctv_lon;
    }


    //parcel 오브젝트 종류
    @Override
    public int describeContents() {
        return 0;
    }
    // 실제 오브젝트
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(cctv_uri);
        dest.writeString(cctv_lat);
        dest.writeString(cctv_lon);
    }
    // 복구하는 생성자 writeToParcel 에서 기록한 순서를 똑같이 해줘야함
    public void readFromParcel(Parcel in) {
        cctv_uri = in.readString();
        cctv_lat = in.readString();
        cctv_lon = in.readString();
    }
    //creator  정의 안하면 에러발생
    //Parcelable protocol requires a Parcelable.Creator object called CREATOR
    //상수 정의 여서 static fianl ?
    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {

        @Override
        public EnrollCCTV createFromParcel(Parcel in) {
            return new EnrollCCTV(in);
        }

        @Override
        public EnrollCCTV[] newArray(int size) {
            // TODO Auto-generated method stub
            return new EnrollCCTV[size];
        }
    };

    public String GetCctv_uri()
    {
        return cctv_uri;
    }
    public String GetCctv_lat()
    {
        return cctv_lat;
    }
    public String GetCctv_lon()
    {
        return cctv_lon;
    }

}