package es.agustruiz.anclapp.ui.newAnchor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class GetBitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {

    public static final String LOG_TAG = GetBitmapFromUrlTask.class.getName() + "[A]";

    protected OnBitmapFromUrlListener mOnBitmapFromUrlListener;
    protected Bitmap mBitmap;

    @Override
    protected Bitmap doInBackground(String... urlStrings) {
        try {
            URL url = new URL(urlStrings[0]);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConn.getInputStream();
                mBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (Exception ignored) {}
        return mBitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        mOnBitmapFromUrlListener.onBitmapReady(bitmap);
    }

    //region [OnBitmapFromUrlListener]

    public interface OnBitmapFromUrlListener{
        void onBitmapReady(Bitmap bitmap);
    }

    public void setBitmapFromUrlListener(OnBitmapFromUrlListener eventListener){
        mOnBitmapFromUrlListener = eventListener;
    }

    //endregion


}
