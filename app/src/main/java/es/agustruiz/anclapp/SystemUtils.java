package es.agustruiz.anclapp;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.Calendar;
import java.util.Locale;

public class SystemUtils {

    public static final String LOG_TAG = SystemUtils.class.getName() + "[A]";
    private static SystemUtils ourInstance = new SystemUtils();

    public static SystemUtils getInstance() {
        return ourInstance;
    }

    public static int getDevideWidth(Context context) {
        Display display =
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int deviceWidth;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }
        return deviceWidth;
    }

    public static int convertDpToPixel(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    public static String getDate(Context context, long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return DateFormat.format(context.getString(R.string.format_date), cal).toString();
    }

    public static String getTime(Context context, long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        return DateFormat.format(context.getString(R.string.format_time), cal).toString();
    }
}
