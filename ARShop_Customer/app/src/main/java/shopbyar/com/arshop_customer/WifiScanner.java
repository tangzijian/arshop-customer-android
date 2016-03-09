package shopbyar.com.arshop_customer;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zijiantang on 26/2/16.
 */
public class WifiScanner {

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private WifiManager mWifiManager;
    private BroadcastReceiver mWifiReceiver;
    private Listener mListener;

    public WifiScanner(Context c) {
        mContext = c;
        mWifiReceiver = new WifiScanReceiver();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
    }

    public void onResume() {
        mContext.registerReceiver(mWifiReceiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    public void onPause() {
        mContext.unregisterReceiver(mWifiReceiver);
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    class WifiScanReceiver extends BroadcastReceiver {

        private Comparator<ScanResult> wificomparator = new Comparator<ScanResult>() {

            @Override
            public int compare(ScanResult arg0, ScanResult arg1) {
                return arg0.level - arg1.level;
            }

        };;

        @SuppressLint("UseValueOf")
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = mWifiManager.getScanResults();

            Collections.sort(wifiScanList, wificomparator);
            Log.i(TAG, "wifi received");
            mListener.setWifiList(wifiScanList);
        }
    }

    interface Listener {
        public void setWifiList(List<ScanResult> wifiScanList);
    }
}

