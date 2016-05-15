package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.widget.Toast;

import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.anchor.NewAnchorActivity;
import es.agustruiz.anclapp.ui.anchor.utils.GetBitmapFromUrlTask;

public class NewAnchorPresenter implements Presenter {

    public static final String LOG_TAG = NewAnchorPresenter.class.getName() + "[A]";

    NewAnchorActivity mActivity;
    Context mContext;
    protected Bitmap mBitmapHeader = null;

    //region [Public methods]

    public NewAnchorPresenter(NewAnchorActivity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
    }

    public void saveNewAnchor() {
        boolean resultOk = false;
        if(isNewAnchorDataOk()) {
            AnchorDAO mAnchorDAO = new AnchorDAO(mContext);
            try {
                Anchor newAnchor = new Anchor(
                        null,
                        mActivity.getLatitudeValue(),
                        mActivity.getLongitudeValue(),
                        mActivity.getTitleValue(),
                        mActivity.getDescriptionValue(),
                        mActivity.getColorValue(),
                        mActivity.getReminderValue(),
                        false, null, false );
                mAnchorDAO.openWritable();
                mAnchorDAO.add(newAnchor);
                resultOk = true;
            } catch (Exception ignored) {
            } finally {
                mAnchorDAO.close();
            }
        }else{
            showMessage("Check anchor data");
        }

        if(resultOk){
            // TODO send message with snackbar?
            Toast.makeText(mContext, "Anchor created!", Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    //endregion

    //region [Presenter overriden methods]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(message);
    }

    //endregion


    //region [Private methods]

    private boolean isNewAnchorDataOk() {
        boolean result = true;
        if (mActivity.getTitleValue().length() == 0) {
            result = false;
        }
        return result;
    }

    public void setHeaderImage() {
        if (mBitmapHeader == null) {
            downloadMapHeaderImage(mActivity.getLatitudeValue(), mActivity.getLongitudeValue(),
                    mActivity.getHeaderWidth(), mActivity.getHeaderHeight());
        } else {
            mActivity.setHeaderBackground(new BitmapDrawable(mActivity.getResources(), mBitmapHeader));
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
