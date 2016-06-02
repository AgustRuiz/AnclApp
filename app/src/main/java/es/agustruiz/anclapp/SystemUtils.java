package es.agustruiz.anclapp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.util.Calendar;

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

    //region [Compat methods]

    public static int getColor(Context context, int resColorId){
        return ContextCompat.getColor(context, resColorId);
    }

    public static void tintDrawable(Drawable drawable, String colorString){{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(Color.parseColor(colorString));
        }else{
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
            wrappedDrawable = wrappedDrawable.mutate();
            DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorString));
        }
    }}

    public static void tintDrawable(Drawable drawable, Context context, int resColorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.setTint(SystemUtils.getColor(context, resColorId));
        }else{
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
            wrappedDrawable = wrappedDrawable.mutate();
            DrawableCompat.setTint(wrappedDrawable, SystemUtils.getColor(context, resColorId));
        }
    }

    public static Drawable getDrawableFromResources(Context context, int resDrawableId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return context.getDrawable(resDrawableId);
        }else{
            return context.getResources().getDrawable(resDrawableId);
        }
    }

    //endregion
}
