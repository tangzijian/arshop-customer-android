package shopbyar.com.arshop_customer;

import android.location.Location;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.google.gson.Gson;

import java.util.List;

/**
 * Created by zijiantang on 25/2/16.
 */
public class ImageMeta {

    private static String TAG = "ImageMeta";

    private static float mLocationMillis = 0;
    private static android.location.Location mLocation;
    private static float mAzimuthPitchRollMillis = 0;
    private static float[] mAzimuthPitchRoll;
    private static float mWifiListMillis = 0;
    private static List<ScanResult> mWifiList;

    public static void setLocation(Location l) {
        mLocation = l;
        mLocationMillis = System.currentTimeMillis();
    }

    public static void setAPR(float[] f) {
        mAzimuthPitchRoll = f;
        mAzimuthPitchRollMillis = System.currentTimeMillis();
    }

    public static void setWifiList(List<ScanResult> l) {
        Log.i(TAG, "set wifi list:" + l.size());
        mWifiList = l;
        mWifiListMillis = System.currentTimeMillis();
    }

    public static String getJSON() {
        Gson gson = new Gson();
        String json = gson.toJson(getObj());
        Log.i(TAG, "converted JSON:" + json);
        return json;
    }

    private static Object getObj() {
        Location newLoc = System.currentTimeMillis() - mLocationMillis <= 1000.0 * 60 * 5
                ? mLocation : null;
        float[] newAPR = System.currentTimeMillis() - mAzimuthPitchRollMillis <= 1000.0 * 5
                ? mAzimuthPitchRoll : null;
        List<ScanResult> newWifiList = System.currentTimeMillis() - mWifiListMillis <= 1000.0 * 60
                ? mWifiList : null;

        return new Obj(mLocation, mAzimuthPitchRoll, mWifiList);
    }

    static class Obj {
        Location GPS;
        float[] AzimuthPitchRoll;
        List<ScanResult> WifiScanList;
        Obj(Location l, float[] apr, List<ScanResult> sc) {
            GPS = l;
            AzimuthPitchRoll = apr;
            WifiScanList = sc;
        }
    }
}
