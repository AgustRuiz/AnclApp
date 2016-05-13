package es.agustruiz.anclapp.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AnclappService extends Service {

    public static final String LOG_TAG = AnclappService.class.getName() + "[A]";

    //region [Variables]

    Context mContext = null;

    //endregion

    //region [Service methods]

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Log.d(LOG_TAG, "onStartCommand");
        mContext = getApplicationContext();
        Thread thread = new CheckAnchorThread(mContext);
        thread.run();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOG_TAG, "onBind");
        return null;
    }

    //endregion
}
