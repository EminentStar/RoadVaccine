package br.com.halyson.ensharp.activity;

/**
 * Created by Jimin on 2015-07-19.
 */
import android.os.Parcel;
import android.os.Parcelable;

public class EnrollData implements Parcelable {

    private String kind; //상황 종류
    private String content; //상황 내용
    private String time; //발생 시간
    private String lat; //위도
    private String lon; //경도
    private String updown; //상하행 정보
    private String used_flag; //팝업여부

    public EnrollData()
    {
    }

    public EnrollData(String kind, String content, String time, String lat, String lon, String updown, String used_flag)
    {
        super();
        this.kind=kind;
        this.content=content;
        this.time=time;
        this.lat=lat;
        this.lon=lon;
        this.updown=updown;
        this.used_flag=used_flag;
    }

    public EnrollData(Parcel in){
        readFromParcel(in);
    }

    public String getkind(){
        return kind;
    }
    public void setkind(String kind){
        this.kind=kind;
    }
    public String getcontent(){
        return content;
    }
    public void setcontent(String content){
        this.content=content;
    }
    public String gettime(){
        return time;
    }
    public void settime(String time){
        this.time=time;
    }
    public String getlat(){
        return lat;
    }
    public void setlat(String lat){
        this.lat=lat;
    }
    public String getlon(){
        return lon;
    }
    public void setlon(String lon){
        this.lon=lon;
    }
    public String getupdown(){
        return updown;
    }
    public void setupdown(String updown){
        this.updown=updown;
    }
    public String getused_flag(){
        return used_flag;
    }
    public void setused_flag(String used_flag){
        this.used_flag=used_flag;
    }

    //parcel 오브젝트 종류
    @Override
    public int describeContents() {
        return 0;
    }
    // 실제 오브젝트
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(kind);
        dest.writeString(content);
        dest.writeString(time);
        dest.writeString(lat);
        dest.writeString(lon);
        dest.writeString(updown);
        dest.writeString(used_flag);
    }
    // 복구하는 생성자 writeToParcel 에서 기록한 순서를 똑같이 해줘야함
    public void readFromParcel(Parcel in) {
        kind = in.readString();
        content = in.readString();
        time = in.readString();
        lat = in.readString();
        lon = in.readString();
        updown = in.readString();
        used_flag=in.readString();
    }
    //creator  정의 안하면 에러발생
    //Parcelable protocol requires a Parcelable.Creator object called CREATOR
    //상수 정의 여서 static fianl ?
    @SuppressWarnings("rawtypes")
    public static final Creator CREATOR = new Creator() {

        @Override
        public EnrollData createFromParcel(Parcel in) {
            return new EnrollData(in);
        }

        @Override
        public EnrollData[] newArray(int size) {
            // TODO Auto-generated method stub
            return new EnrollData[size];
        }
    };

    public String GetKind()
    {
        return kind;
    }
    public String GetContent()
    {
        return content;
    }
    public String GetTime()
    {
        return time;
    }
    public String GetLat()
    {
        return lat;
    }
    public String GetLon()
    {
        return lon;
    }
    public String GetUpdown()
    {
        return updown;
    }
    public String GetUsed_flag()
    {
        return used_flag;
    }

}