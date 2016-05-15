package es.agustruiz.anclapp.service;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.List;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.anchor.SeeAnchorActivity;

public class CheckAnchorRunnable implements Runnable {

    public static final String LOG_TAG = CheckAnchorRunnable.class.getName() + "[A]";
    private static final int SLEEP_THREAD_DELAY_MILLIS = 10000;
    private static final Long LOCATION_REQUEST_MIN_TIME = 5000L;
    private static final Float LOCATION_REQUEST_MIN_DISTANCE = 100F;

    //region [Variables]

    Context mContext;
    AnchorDAO mAnchorDAO;
    SharedPreferences mPreferences;
    LocationManager mLocationManager;
    Location mLastLocation = null;

    //endregion

    //region [Public methods]

    public CheckAnchorRunnable(Context context) {
        mContext = context;
        mAnchorDAO = new AnchorDAO(mContext);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        prepareLocationService();
        Log.d(LOG_TAG, "CheckAnchorRunnable started");
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "Starting looper");
        do {
            if (mLastLocation != null) {
                Log.d(LOG_TAG, "Current location OK");
                Anchor.setReferenceLocation(mLastLocation);
                int reminderDistanceMetres = Integer.parseInt(
                        mPreferences.getString(
                                mContext.getString(R.string.key_pref_location_reminder_distance),
                                mContext.getString(R.string.pref_location_reminder_distance_default_value)
                        ));
                mAnchorDAO.openReadOnly();
                List<Anchor> anchorList = mAnchorDAO.getAllReminderEnabled();
                mAnchorDAO.close();
                for (Anchor anchor : anchorList) {
                    Log.d(LOG_TAG, String.format("Anchor \"%s\"", anchor.getTitle()));
                    if (anchor.getDistanceInKms() * 1000 < reminderDistanceMetres) {
                        if (!anchor.isNotify()) {
                            Log.d(LOG_TAG, String.format("New notify \"%s\"", anchor.getTitle()));
                            saveAnchorNofity(anchor, true);
                            Intent intent = new Intent(mContext, SeeAnchorActivity.class);
                            intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, anchor.getId());
                            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                            Notificator.showNotification(mContext,
                                    String.format(mContext.getString(R.string.msg_near_anchor),
                                            anchor.getTitle()),
                                    anchor.getId(),
                                    Color.parseColor(anchor.getColor()),
                                    pendingIntent
                            );
                        } else {
                            Log.d(LOG_TAG, String.format("Notify of \"%s\" is already shown", anchor.getTitle()));
                        }
                    } else {
                        Log.d(LOG_TAG, String.format("Anchor \"%s\" is out of range", anchor.getTitle()));
                        saveAnchorNofity(anchor, false);
                    }
                }
            } else {
                Log.d(LOG_TAG, "No location found\n");
            }
            Log.d(LOG_TAG, "End of loop\n\n");
            sleepThread(SLEEP_THREAD_DELAY_MILLIS);
        } while (true);
    }

    //endregion

    //region [Private methods]

    private void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void prepareLocationService() {
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        String mLocationProvider = mLocationManager.getBestProvider(getCriteria(), true);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && mLocationProvider != null) {
            mLocationManager.requestLocationUpdates(
                    mLocationProvider,
                    LOCATION_REQUEST_MIN_TIME,
                    LOCATION_REQUEST_MIN_DISTANCE,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(LOG_TAG, "Location changed");
                            mLastLocation = location;
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {
                            Log.d(LOG_TAG, "Location provicer enabled");
                        }

                        @Override
                        public void onProviderDisabled(String provider) {
                            Log.d(LOG_TAG, "Location provicer disabled");
                            mLastLocation = null;
                        }
                    }
            );
            //} else {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
    }

    private void saveAnchorNofity(Anchor anchor, boolean state) {
        anchor.setNotify(state);
        mAnchorDAO.openWritable();
        mAnchorDAO.update(anchor);
        mAnchorDAO.close();
    }

    private static Criteria getCriteria() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_MEDIUM);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        return criteria;
    }

    //endregion
}
