package es.agustruiz.anclapp.service;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;

public class CheckAnchorThread extends Thread implements Runnable {

    public static final String LOG_TAG = CheckAnchorThread.class.getName() + "[A]";

    private static final int SLEEP_THREAD_DELAY_MILLIS = 10000;

    Context mContext;
    AnchorDAO mAnchorDAO;
    SharedPreferences mPreferences;
    LocationManager mLocationManager;
    String mLocationProvider;
    List<Long> mAnchorNotified;

    public CheckAnchorThread(Context context) {
        mContext = context;
        mAnchorDAO = new AnchorDAO(mContext);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationProvider = getBestLocationProvider();
        mAnchorNotified = new ArrayList<>();
    }

    @Override
    public void run() {
        do {
            Location currentLocation = getLastKnownLocation();
            if (currentLocation != null) {
                //Log.d(LOG_TAG, "Current location correct");
                Anchor.setReferenceLocation(currentLocation);
                int reminderDistanceMetres = Integer.parseInt(
                        mPreferences.getString(
                                mContext.getString(R.string.key_pref_location_reminder_distance),
                                mContext.getString(R.string.pref_location_reminder_distance_default_value)
                        ));
                //Log.d(LOG_TAG, String.format("Reminder distance: " + reminderDistanceMetres));
                mAnchorDAO.openReadOnly();
                List<Anchor> anchorList = mAnchorDAO.getAllReminderEnabled();
                mAnchorDAO.close();
                //Log.d(LOG_TAG, "Anchor list loaded (" + anchorList.size() + ")");
                for (Anchor anchor : anchorList) {
                    //Log.d(LOG_TAG, String.format("Anchor \"%s\" at %fkms", anchor.getTitle(), anchor.getDistanceInKms()));
                    if (anchor.getDistanceInKms() * 1000 < reminderDistanceMetres) {
                        //Log.d(LOG_TAG, "Anchor in range");
                        if (!mAnchorNotified.contains(anchor.getId())) {
                            mAnchorNotified.add(anchor.getId());
                            //Log.d(LOG_TAG, "Boom! Notification!");
                            Notificator.showNotification(mContext,
                                    String.format(mContext.getString(R.string.msg_near_anchor),
                                            anchor.getTitle()),
                                    anchor.getId(),
                                    Color.parseColor(anchor.getColor())
                            );
                        }
                    } else {
                        //Log.d(LOG_TAG, "Anchor out of range");
                        mAnchorNotified.remove(anchor.getId());
                    }
                }
                //} else {
                //Log.d(LOG_TAG, "Current location not found");
            }
            sleepThread(SLEEP_THREAD_DELAY_MILLIS);
        } while (true);
    }

    private void sleepThread(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private String getBestLocationProvider() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        return mLocationManager.getBestProvider(criteria, true);
    }

    private Location getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        } else {
            return mLocationManager.getLastKnownLocation(mLocationProvider);
        }
    }
}
