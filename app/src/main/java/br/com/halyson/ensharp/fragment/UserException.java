package br.com.halyson.ensharp.fragment;

/**
 * Created by LEE on 2015-07-20.
 */
public class UserException {

    public UserException() {
    }

    public static boolean OnPopup = false;
    public static boolean CCTVOnPopup = false;
    public static boolean SleepOnPopup = false;

    public static double calDistance(double lat1, double lon1, double lat2, double lon2) {

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

    public static double deg2rad(double deg) {
        return (Double) (deg * Math.PI / (Double) 180d);
    }

    public static double rad2deg(double rad) {
        return (Double) (rad * (Double) 180d / Math.PI);
    }

    public boolean getAPI() {
        boolean result = true;
        return result;
    }

    public int getMinCount(float maxSpeed) {
        int result = 0;

        if (maxSpeed >= 100) result = 6;
        else if (maxSpeed >= 80) result = 5;
        else if (maxSpeed >= 60) result = 4;

        return result;
    }

    public float msTokh(float currentSpeed) {
        float result;
        result = (float) (currentSpeed * 3.6);
        return result;
    }


}
