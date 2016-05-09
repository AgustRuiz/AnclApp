package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.SeeAnchorPresenter;

public class SeeAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = SeeAnchorActivity.class.getName() + "[A]";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_collapsing)
    CollapsingToolbarLayout mCollapsingToolbar;

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

    @Bind(R.id.btn_navigate)
    Button mBtnNavigate;

    protected long mIntentAnchorId;
    public static final String ANCHOR_ID_INTENT_TAG = "mIntentAnchorId";

    protected Context mContext;
    protected ActionBar mActionBar;
    protected SeeAnchorPresenter mPresenter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_anchor);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mPresenter = new SeeAnchorPresenter(this);
        getIntentExtras(getIntent());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initialize();
        fillData(mPresenter.refreshAnchor(mIntentAnchorId));
        tintElementsWithAnchorColor(Color.parseColor(mPresenter.getAnchor(mIntentAnchorId).getColor()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.see_anchor, menu);
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mPresenter.setHeaderImage();
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
        Snackbar.make(mCollapsingToolbar, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public void setHeaderBackground(Drawable background) {
        mCollapsingToolbar.setBackground(background);
    }

    public Drawable getHeaderBackground() {
        return mCollapsingToolbar.getBackground();
    }

    public int getHeaderWidth() {
        return mCollapsingToolbar.getWidth();
    }

    public int getHeaderHeight() {
        return mCollapsingToolbar.getHeight();
    }

    //region [Private methods]

    private void getIntentExtras(Intent intent) {
        mIntentAnchorId = intent.getLongExtra(ANCHOR_ID_INTENT_TAG, Long.MIN_VALUE);
    }

    private void initialize() {
        setSupportActionBar(mToolbar);
        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
        mFabEditAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editAnchor();
            }
        });
        mBtnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.navigateToPlace(mIntentAnchorId);
            }
        });
    }

    private void fillData(Anchor anchor) {
        if (anchor != null) {
            mCollapsingToolbar.setTitle(anchor.getTitle());
            mTextViewDescription.setText(anchor.getDescription());
            mTextViewLatLng.setText(anchor.getLatitude() + ", " + anchor.getLongitude());
            if (anchor.isReminder()) {
                mTextViewReminder.setText(R.string.reminder_location_enabled);
                mImageViewReminderIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_black_24dp, getTheme()));
                mImageViewReminderIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.colorAccent)));
            } else {
                mTextViewReminder.setText(R.string.reminder_location_disabled);
                mImageViewReminderIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp, getTheme()));
                mImageViewReminderIcon.setImageTintList(ColorStateList.valueOf(getColor(R.color.grey700)));
            }
            mImageViewColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(anchor.getColor())));
        }
    }

    private void tintElementsWithAnchorColor(int color) {
        mCollapsingToolbar.setBackgroundColor(color);
        mFabEditAnchor.setBackgroundTintList(ColorStateList.valueOf(color));
        mToolbarMarkerIcon.setImageTintList(ColorStateList.valueOf(color));
    }

    //endregion
}
