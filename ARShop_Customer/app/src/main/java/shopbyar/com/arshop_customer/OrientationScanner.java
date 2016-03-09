package shopbyar.com.arshop_customer;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.util.ArrayList;

/**
 * Created by zijiantang on 26/2/16.
 */
public class OrientationScanner implements
        SensorEventListener {

    private final Context mContext;
    private String TAG = this.getClass().getSimpleName();
    private SensorManager mSensorManager;
    private Sensor mOrientation;

    public OrientationScanner(Context c) {
        mContext = c;
    }
    public void onCreate() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
        // You must implement this callback in your code.
    }

    protected void onResume() {
        mSensorManager.registerListener(this, mOrientation,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        mSensorManager.unregisterListener(this);
    }

    /*
     * Azimuth (degrees of rotation around the z axis). This is the angle between magnetic north
     * and the device's y axis. For example, if the device's y axis is aligned with magnetic north
     * this value is 0, and if the device's y axis is pointing south this value is 180. Likewise,
     * when the y axis is pointing east this value is 90 and when it is pointing west this value is
     * 270.
     *
     * Pitch (degrees of rotation around the x axis). This value is positive when the positive z
     * axis rotates toward the positive y axis, and it is negative when the positive z axis rotates
     * toward the negative y axis. The range of values is 180 degrees to -180 degrees.
     *
     * Roll (degrees of rotation around the y axis). This value is positive when the positive z axis
     * rotates toward the positive x axis, and it is negative when the positive z axis rotates
     * toward the negative x axis. The range of values is 90 degrees to -90 degrees.
     */

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Log.i(TAG, event + "");
        for (int i = 0; i < mListeners.size(); i++) {
            mListeners.get(i).setAzimuthPitchRoll(event.values);
        }
    }

    private ArrayList<Listener> mListeners = new ArrayList<Listener>();

    public void setListener(Listener l) {
        mListeners.add(l);
    }

    public interface Listener {
        public void setAzimuthPitchRoll(float[] apr);
    }
}
