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
import android.location.LocationManager;
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

    //region [Variables]

    Context mContext;
    AnchorDAO mAnchorDAO;
    SharedPreferences mPreferences;
    LocationManager mLocationManager;
    String mLocationProvider;

    //endregion

    //region [Public methods]

    public CheckAnchorRunnable(Context context) {
        mContext = context;
        mAnchorDAO = new AnchorDAO(mContext);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationProvider = getBestLocationProvider();
        Log.d(LOG_TAG, "CheckAnchorRunnable started");
    }

    @Override
    public void run() {
        Log.d(LOG_TAG, "Starting looper");
        do {
            Location currentLocation = getLastKnownLocation();
            if (currentLocation != null) {
                Log.d(LOG_TAG, "Current location OK");
                Anchor.setReferenceLocation(currentLocation);
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
            }else{
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

    private void saveAnchorNofity(Anchor anchor, boolean state) {
        anchor.setNotify(state);
        mAnchorDAO.openWritable();
        mAnchorDAO.update(anchor);
        mAnchorDAO.close();
    }

    //endregion
}
