package es.agustruiz.anclapp;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

public class SystemUtils {

    public static final String LOG_TAG = SystemUtils.class.getName()+"[A]";
    private static SystemUtils ourInstance = new SystemUtils();

    public static SystemUtils getInstance(){
        return ourInstance;
    }

    public static float getDevideWidth(Context context){
        Display display =
                ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int deviceWidth;
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
            Point size = new Point();
            display.getSize(size);
            deviceWidth = size.x;
        } else {
            deviceWidth = display.getWidth();
        }
        return deviceWidth;
    }

}
