package es.agustruiz.anclapp.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import es.agustruiz.anclapp.R;

public class Notificator {

    public static final String LOG_TAG = Notificator.class.getName() + "[A]";

    //region [Public static methods]

    public static void showNotification(Context context, String title, Long anchorId, int anchorColor) {
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setSmallIcon(R.drawable.ic_notification_icon);
        mBuilder.setColor(anchorColor);
        mBuilder.setContentTitle(title);
        mBuilder.setContentText(context.getString(R.string.msg_click_more_options));
        mBuilder.setWhen(System.currentTimeMillis());
        mBuilder.setDefaults(Notification.DEFAULT_SOUND
                | Notification.DEFAULT_LIGHTS
                | Notification.DEFAULT_VIBRATE);

        mNotificationManager.notify(Integer.parseInt(anchorId.toString()), mBuilder.build());
        //Log.d(LOG_TAG, "notification!");
    }

    //endregion

}
