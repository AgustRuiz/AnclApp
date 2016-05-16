package es.agustruiz.anclapp.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;

import java.net.URI;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;

public class ShareAnchor {

    public static final String LOG_TAG = ShareAnchor.class.getName() + "[A]";

    public static final String TITLE_SHARE_INTENT_TAG = "titleShareIntentTag";
    public static final String DESCRIPTION_SHARE_INTENT_TAG = "descriptionShareIntentTag";
    public static final String LATITUDE_SHARE_INTENT_TAG = "latitudeShareIntentTag";
    public static final String LONGITUDE_SHARE_INTENT_TAG = "longitudeShareIntentTag";
    public static final String COLOR_SHARE_INTENT_TAG = "colorShareIntentTag";

    private static final String URI_GMAPS_SCHEME ="http";
    private static final String URI_GMAPS_AUTHORITY ="maps.google.com";
    private static final String URI_GMAPS_PATH = "maps";
    private static final String URI_GMAPS_PARAM_LOCATION = "saddr";

    public static void shareAnchor(Context context, Anchor anchor) {
        Intent baseIntent = new Intent(android.content.Intent.ACTION_SEND);
        baseIntent.setType("text/plain");
        baseIntent.putExtra(android.content.Intent.EXTRA_TEXT, getMessage(anchor));
        Intent shareIntent = Intent.createChooser(baseIntent, context.getString(R.string.share_via));
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(shareIntent);
    }

    private static String getUriString(Anchor anchor){
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.scheme(URI_GMAPS_SCHEME)
                .authority(URI_GMAPS_AUTHORITY)
                .appendPath(URI_GMAPS_PATH)
                .appendQueryParameter(URI_GMAPS_PARAM_LOCATION, anchor.getLatitude()+","+anchor.getLongitude());
        return uriBuilder.build().toString();
    }

    private static String getMessage(Anchor anchor){
        String message = "I want to share an anchor with you.\n\n"
                + "Title: " + anchor.getTitle() + "\n"
                + "Description: " + anchor.getDescription() + "\n\n"
                + getUriString(anchor);
        return message;
    }

}
