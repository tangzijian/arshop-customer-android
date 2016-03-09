package shopbyar.com.arshop_customer;

import android.util.Log;
import android.widget.Toast;

/**
 * Created by zijiantang on 25/2/16.
 */
public class PermissionCheck {
    public static MainActivity mActivity;
    private static String TAG = "PermissionCheck";

    public static void init(MainActivity activity) {
        mActivity = activity;
    }

    public static void showToast(String msg) {
        Log.i(TAG, "showToast:" + msg);
        Toast.makeText(mActivity, msg, Toast.LENGTH_LONG).show();
    }

    public static void showToastAndClose(String msg) {
        showToast("Make sure you have granted enough permissions to this app");
        mActivity.drawer.setSelection(2, true);
    }
}
