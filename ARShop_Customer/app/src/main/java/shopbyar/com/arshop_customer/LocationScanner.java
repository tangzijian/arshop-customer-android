package shopbyar.com.arshop_customer;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by zijiantang on 26/2/16.
 */
public class LocationScanner implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private String TAG = this.getClass().getSimpleName();
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Listener mListener;

    public LocationScanner(Context c) {
        mContext = c;
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public interface Listener {
        void set(Location location);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        Log.i(TAG, "buildGoogleApiClient");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "onConnected");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(
                        mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                PermissionCheck.showToastAndClose("Make sure you have granted enough permissions to this app");
            } catch (Exception other) {
                other.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onConnectionFailed");
    }

    public void onCreate() {
        Log.i(TAG, "onCreated");
        try {
            buildGoogleApiClient();
            createLocationRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onStart() {
        if (mGoogleApiClient != null) {
            try {
                mGoogleApiClient.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "onStart");
    }

    public void onStop() {
        if (mGoogleApiClient != null) {
            try {
                mGoogleApiClient.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.i(TAG, "onStop");
    }

    public void onResume() {
        Log.i(TAG, "onResume");
        if (mGoogleApiClient != null && mLocationRequest != null) {
            try {
                if (mGoogleApiClient.isConnected()) {
                    LocationServices.FusedLocationApi.requestLocationUpdates(
                            mGoogleApiClient, mLocationRequest, this);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onPause() {
        Log.i(TAG, "onPause");
        if (mGoogleApiClient != null) {
            try {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i(TAG, "onLocationChanged " + location.toString());
        if (mListener != null) {
            try {
                mListener.set(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
