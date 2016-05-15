package es.agustruiz.anclapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AnclappBootReceiver extends BroadcastReceiver {

    public static final String LOG_TAG = AnclappBootReceiver.class.getName() + "[A]";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(LOG_TAG, "Starting AnclappService...");
        Intent serviceIntent = new Intent(context, AnclappService.class);
        context.startService(serviceIntent);
    }

}
