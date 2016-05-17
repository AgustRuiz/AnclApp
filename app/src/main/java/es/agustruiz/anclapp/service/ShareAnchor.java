package es.agustruiz.anclapp.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;

public class ShareAnchor {

    public static final String LOG_TAG = ShareAnchor.class.getName() + "[A]";

    private static final String URI_SCHEME ="http";
    private static final String URI_AUTHORITY ="agustruiz.es";
    private static final String URI_PATH="anclapp";
    public static final String URI_PARAM_TITLE = "t";
    public static final String URI_PARAM_DESCRIPTION = "d";
    public static final String URI_PARAM_LATITUDE = "lat";
    public static final String URI_PARAM_LONGITUDE = "lng";

    public static void shareAnchor(Context context, Anchor anchor) {
        Intent baseIntent = new Intent(android.content.Intent.ACTION_SEND);
        baseIntent.setType("text/plain");
        baseIntent.putExtra(android.content.Intent.EXTRA_TEXT, getMessage(context, anchor));
        Intent shareIntent = Intent.createChooser(baseIntent, context.getString(R.string.share_via));
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    private static String getUriString(Anchor anchor){
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(URI_SCHEME)
                .authority(URI_AUTHORITY)
                .path(URI_PATH)
                .appendQueryParameter(URI_PARAM_LATITUDE, anchor.getLatitude().toString())
                .appendQueryParameter(URI_PARAM_LONGITUDE, anchor.getLongitude().toString())
                .appendQueryParameter(URI_PARAM_TITLE, anchor.getTitle())
                .appendQueryParameter(URI_PARAM_DESCRIPTION, anchor.getDescription());
        return uriBuilder.build().toString();
    }

    private static String getMessage(Context context, Anchor anchor){
        return context.getString(R.string.msg_share_anchor, anchor.getTitle(), anchor.getDescription(), getUriString(anchor));
    }

}
