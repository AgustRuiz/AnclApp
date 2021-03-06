package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.service.ShareAnchor;
import es.agustruiz.anclapp.ui.anchor.EditAnchorActivity;
import es.agustruiz.anclapp.ui.anchor.SeeAnchorActivity;
import es.agustruiz.anclapp.internet.GetBitmapFromUrlTask;

public class SeeAnchorPresenter implements IPresenter {

    public static final String LOG_TAG = SeeAnchorPresenter.class.getName() + "[A]";

    protected SeeAnchorActivity mActivity;
    protected Context mContext;
    protected AnchorDAO mAnchorDAO = null;
    protected Anchor mAnchor = null;
    protected Bitmap mBitmapHeader = null;

    //region [Public methods]

    public SeeAnchorPresenter(SeeAnchorActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public Anchor refreshAnchor(long mIntentAnchorId) {
        mAnchor = null;
        getAnchor(mIntentAnchorId);
        return mAnchor;
    }

    public Anchor getAnchor(long mIntentAnchorId) {
        if (mAnchor == null) {
            //Log.d(LOG_TAG, "get anchor from DB, id=" + mIntentAnchorId);
            prepareDAO();
            mAnchorDAO.openReadOnly();
            mAnchor = mAnchorDAO.get(mIntentAnchorId);
            mAnchorDAO.close();
        }
        return mAnchor;
    }

    public void editAnchor() {
        Intent intent = new Intent(mContext, EditAnchorActivity.class);
        intent.putExtra(EditAnchorActivity.ID_INTENT_TAG, mAnchor.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public void restoreAnchor() {
        prepareDAO();
        mAnchorDAO.openWritable();
        mAnchorDAO.restore(mAnchor);
        mAnchorDAO.close();

        Toast.makeText(mContext, mContext.getString(R.string.msg_anchor_restored), Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(mContext, SeeAnchorActivity.class);
        intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, mAnchor.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);
    }

    public void purgeAnchor() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage(mContext.getString(R.string.msg_remove_anchor));
        alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.action_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareDAO();
                        mAnchorDAO.openWritable();
                        mAnchorDAO.delete(mAnchor);
                        mAnchorDAO.close();
                        mActivity.onBackPressed();
                    }
                });
        alertDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.action_cancel), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void shareAnchor() {
        ShareAnchor.shareAnchor(mContext, mAnchor);
    }

    public void removeAnchor() {
        //Log.d(LOG_TAG, "removeAnchor");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mActivity);
        alertDialogBuilder.setMessage(mContext.getString(R.string.msg_remove_anchor));
        alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.action_accept),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        prepareDAO();
                        mAnchorDAO.openWritable();
                        mAnchorDAO.moveToBin(mAnchor);
                        mAnchorDAO.close();
                        mActivity.onBackPressed();
                    }
                });
        alertDialogBuilder.setNegativeButton(mContext.getResources().getString(R.string.action_cancel), null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void setHeaderImage() {
        if (mBitmapHeader == null) {
            downloadMapHeaderImage(mAnchor.getLatitude(), mAnchor.getLongitude(),
                    mActivity.getHeaderWidth(), mActivity.getHeaderHeight());
        } else {
            mActivity.setHeaderBackground(new BitmapDrawable(mActivity.getResources(), mBitmapHeader));
        }
    }

    public void navigateToPlace(long anchorId) {
        Anchor anchor = getAnchor(anchorId);
        double latitude = anchor.getLatitude();
        double longitude = anchor.getLongitude();
        String label = anchor.getTitle();
        String uriBegin = "geo:" + latitude + "," + longitude;
        String query = latitude + "," + longitude + "(" + label + ")";
        String encodedQuery = Uri.encode(query);
        String uriStrign = uriBegin + "?q=" + encodedQuery; //+"&z=16";
        Uri uri = Uri.parse(uriStrign);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }

    public boolean isDeleted(long anchorId) {
        return getAnchor(anchorId).isDeleted();
    }

    //endregion

    //region [IPresenter overriden methods]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(message);
    }

    //endregion

    //region [Private methods]

    protected void prepareDAO() {
        if (mAnchorDAO == null) {
            mAnchorDAO = new AnchorDAO(mContext);
        }
    }

    private void downloadMapHeaderImage(Double latitude, Double longitude, final int width, final int height) {

        // Note: Added a margin on top and bottom to trim Google logo and keep map in center

        int scaleWidthFactor = (int) Math.ceil((float) width / (float) GetBitmapFromUrlTask.MAX_GOOGLE_STATIC_MAP);
        int scaleHeightFactor = (int) Math.ceil((float) height / (float) GetBitmapFromUrlTask.MAX_GOOGLE_STATIC_MAP);
        final int scaleFactor = Math.max(scaleWidthFactor, scaleHeightFactor) > 0 ? Math.max(scaleWidthFactor, scaleHeightFactor) : 1;

        int scaledWidth = (int) Math.ceil(width / scaleFactor);
        int scaledHeight = (int) Math.ceil(height / scaleFactor) + GetBitmapFromUrlTask.TRIM_MAP_MARGIN * scaleFactor * 2;

        int zoom = 16;

        String urlString = "http://maps.google.com/maps/api/staticmap?center=" + latitude.toString() + ","
                + longitude.toString() + "&zoom=" + zoom + "&size=" + scaledWidth + "x"
                + scaledHeight + "&scale=" + scaleFactor + "&sensor=false";

        GetBitmapFromUrlTask getHeaderTask = new GetBitmapFromUrlTask();
        getHeaderTask.setBitmapFromUrlListener(new GetBitmapFromUrlTask.OnBitmapFromUrlListener() {
            @Override
            public void onBitmapReady(Bitmap bitmapFull) {
                if (bitmapFull != null) {
                    mBitmapHeader = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Paint p = new Paint();
                    p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    Canvas c = new Canvas(mBitmapHeader);
                    c.drawRect(
                            0, GetBitmapFromUrlTask.TRIM_MAP_MARGIN * scaleFactor,
                            0, GetBitmapFromUrlTask.TRIM_MAP_MARGIN * scaleFactor, p);
                    c.drawBitmap(bitmapFull, 0, 0, null);

                    // Soft transition
                    Drawable backgrounds[] = new Drawable[]{
                            mActivity.getHeaderBackground(),
                            new BitmapDrawable(mActivity.getResources(), mBitmapHeader)
                    };
                    TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                    mActivity.setHeaderBackground(crossfader);
                    crossfader.startTransition(GetBitmapFromUrlTask.HEADER_TRANSITION_DURATION);
                }
            }
        });
        getHeaderTask.execute(urlString);
    }

    //endregion
}
