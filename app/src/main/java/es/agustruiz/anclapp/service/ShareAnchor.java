package es.agustruiz.anclapp.service;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;

import es.agustruiz.anclapp.model.Anchor;

public class ShareAnchor {

    public static final String LOG_TAG = ShareAnchor.class.getName() + "[A]";

    public static final String TITLE_SHARE_INTENT_TAG = "titleShareIntentTag";
    public static final String DESCRIPTION_SHARE_INTENT_TAG = "descriptionShareIntentTag";
    public static final String LATITUDE_SHARE_INTENT_TAG = "latitudeShareIntentTag";
    public static final String LONGITUDE_SHARE_INTENT_TAG = "longitudeShareIntentTag";
    public static final String COLOR_SHARE_INTENT_TAG = "colorShareIntentTag";

    public static void shareAnchor(Context context, Anchor anchor) {
        ShareActionProvider shareActionProvider = new ShareActionProvider(context);
        Log.d(LOG_TAG, "Sending share intent");
        if (shareActionProvider != null && anchor != null) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(ShareAnchor.LATITUDE_SHARE_INTENT_TAG, anchor.getLatitude());
            sendIntent.putExtra(ShareAnchor.LONGITUDE_SHARE_INTENT_TAG, anchor.getLongitude());
            sendIntent.putExtra(ShareAnchor.TITLE_SHARE_INTENT_TAG, anchor.getTitle());
            sendIntent.putExtra(ShareAnchor.DESCRIPTION_SHARE_INTENT_TAG, anchor.getDescription());
            sendIntent.putExtra(ShareAnchor.COLOR_SHARE_INTENT_TAG, anchor.getColor());
            sendIntent.setType("text/plain");
            shareActionProvider.setShareIntent(sendIntent);
            Log.d(LOG_TAG, "Sent share intent");
        } else {
            Log.d(LOG_TAG, "Erró");
        }
    }

}
