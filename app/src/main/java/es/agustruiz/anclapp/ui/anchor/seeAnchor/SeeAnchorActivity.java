package es.agustruiz.anclapp.ui.anchor.seeAnchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.SeeAnchorPresenter;
import es.agustruiz.anclapp.ui.anchor.GetBitmapFromUrlTask;

public class SeeAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = SeeAnchorActivity.class.getName() + "[A]";
    private final int HEADER_TRANSITION_DURATION = 500;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @Bind(R.id.toolbar_marker_icon)
    ImageView mToolbarMarkerIcon;

    @Bind(R.id.fab_edit_anchor)
    FloatingActionButton mFabEditAnchor;

    @Bind(R.id.see_anchor_description)
    TextView mTextViewDescription;

    @Bind(R.id.see_anchor_lat_lng)
    TextView mTextViewLatLng;

    @Bind(R.id.see_anchor_tag)
    TextView mTextViewTag;

    @Bind(R.id.see_anchor_reminder_icon)
    ImageView mImageViewReminderIcon;

    @Bind(R.id.see_anchor_reminder_label)
    TextView mTextViewReminder;

    @Bind(R.id.see_anchor_color_icon)
    ImageView mImageViewColorIcon;

    @Bind(R.id.see_anchor_color_text)
    TextView mTextViewColor;

    protected long mIntentAnchorId;
    public static final String ANCHOR_ID_INTENT_TAG = "mIntentAnchorId";

    protected Context mContext;
    protected SeeAnchorPresenter mPresenter = null;
    protected AnchorDAO mAnchorDAO;
    protected Anchor mAnchor = null;

    Bitmap mBitmapHeader = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_anchor);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mPresenter = new SeeAnchorPresenter(this);

        getIntentExtras(getIntent());

        mAnchorDAO = new AnchorDAO(mContext);
        mAnchorDAO.openReadOnly();
        mAnchor = mAnchorDAO.get(mIntentAnchorId);
        mAnchorDAO.close();

        initialize();
        fillData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_anchor, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (mBitmapHeader != null) {
            mToolbarLayout.setBackground(new BitmapDrawable(getResources(), mBitmapHeader));
        } else {
            getMapHeaderImage(mAnchor.getLatitude(), mAnchor.getLongitude());
        }
        tintElementsWithAnchorColor();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                mPresenter.editAnchor();
                return true;
            case R.id.action_remove:
                mPresenter.removeAnchor();
                return true;
            default:
                return false;
        }
    }

    public void showMessageView(String message) {
        Snackbar.make(mToolbarLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    //region [Private methods]

    private void getIntentExtras(Intent intent) {
        mIntentAnchorId = intent.getLongExtra(ANCHOR_ID_INTENT_TAG, Long.MIN_VALUE);
    }

    private void initialize() {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mFabEditAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editAnchor();
            }
        });
    }

    private void fillData() {
        if (mAnchor != null) {
            setTitle(mAnchor.getTitle()); //mTextViewTitle.setText(mAnchor.getTitle());
            mTextViewDescription.setText(mAnchor.getDescription());
            mTextViewLatLng.setText(mAnchor.getLatitude() + ", " + mAnchor.getLongitude());
            if (mAnchor.isReminder()) {
                mTextViewReminder.setText(R.string.reminder_location_enabled);
                mImageViewReminderIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
            } else {
                mTextViewReminder.setText(R.string.reminder_location_disabled);
                mImageViewReminderIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp, getTheme()));
            }
            mImageViewColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(mAnchor.getColor())));
        }
    }

    private void getMapHeaderImage(Double latitude, Double longitude) {

        // Note: Added a margin on top and bottom to trim Google logo and keep map in center

        final int width = mToolbarLayout.getWidth();
        final int height = mToolbarLayout.getHeight();

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
                            mToolbarLayout.getBackground(),
                            new BitmapDrawable(getResources(), mBitmapHeader)
                    };
                    TransitionDrawable crossfader = new TransitionDrawable(backgrounds);
                    mToolbarLayout.setBackground(crossfader);
                    crossfader.startTransition(HEADER_TRANSITION_DURATION);
                }
            }
        });
        getHeaderTask.execute(urlString);
    }

    private void tintElementsWithAnchorColor() {
        int color = Color.parseColor(mAnchor.getColor());
        mToolbarLayout.setBackgroundColor(color);
        mFabEditAnchor.setBackgroundTintList(ColorStateList.valueOf(color));
        mToolbarMarkerIcon.setImageTintList(ColorStateList.valueOf(color));
    }

    //endregion
}
