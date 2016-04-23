package es.agustruiz.anclapp.ui.newAnchor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import es.agustruiz.anclapp.event.EventsUtil;

public class GetBitmapFromUrlTask extends AsyncTask<String, Void, Bitmap> {

    public static final String LOG_TAG = GetBitmapFromUrlTask.class.getName() + "[A]";

    @Override
    protected Bitmap doInBackground(String... urlStrings) {
        Bitmap bitmap = null;
        try {
            URL url = new URL(urlStrings[0]);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = httpConn.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
        } catch (Exception ignored) {}
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        EventsUtil.getInstance().setToolbarLayoutBitmap(bitmap);
    }
}
