package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.SeeAnchorPresenter;

public class SeeAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = SeeAnchorActivity.class.getName() + "[A]";

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_collapsing)
    CollapsingToolbarLayout mCollapsingToolbar;

    @BindView(R.id.toolbar_marker_icon)
    ImageView mToolbarMarkerIcon;

    @BindView(R.id.fab_action_anchor)
    FloatingActionButton mFabActionAnchor;

    @BindView(R.id.see_anchor_line_deleted_date)
    LinearLayoutCompat mLinearLayoutDeletedDate;

    @BindView(R.id.see_anchor_deleted_date)
    TextView mTextViewDeletedDate;

    @BindView(R.id.see_anchor_description)
    TextView mTextViewDescription;

    @BindView(R.id.see_anchor_lat_lng)
    TextView mTextViewLatLng;

    @BindView(R.id.see_anchor_tag)
    TextView mTextViewTag;

    @BindView(R.id.see_anchor_reminder_icon)
    ImageView mImageViewReminderIcon;

    @BindView(R.id.see_anchor_reminder_label)
    TextView mTextViewReminder;

    @BindView(R.id.see_anchor_color_icon)
    ImageView mImageViewColorIcon;

    @BindView(R.id.see_anchor_color_text)
    TextView mTextViewColor;

    @BindView(R.id.btn_navigate)
    Button mBtnNavigate;

    @BindView(R.id.see_anchor_line_buttons)
    LinearLayoutCompat mLinearLayoutButtons;

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
        super.onCreateOptionsMenu(menu);
        if (mPresenter.isDeleted(mIntentAnchorId)) {
            getMenuInflater().inflate(R.menu.see_deleted_anchor, menu);
        } else {
            getMenuInflater().inflate(R.menu.see_anchor, menu);
            MenuItem shareMenuItem = menu.findItem(R.id.action_share);
            Drawable iconShareMenuItem = shareMenuItem.getIcon();
            iconShareMenuItem.mutate();
            SystemUtils.tintDrawable(iconShareMenuItem, mContext, android.R.color.white);
        }
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
            case R.id.action_restore:
                mPresenter.restoreAnchor();
                return true;
            case R.id.action_edit:
                mPresenter.editAnchor();
                return true;
            case R.id.action_remove:
                mPresenter.removeAnchor();
                return true;
            case R.id.action_share:
                Log.d(LOG_TAG, "Button share");
                mPresenter.shareAnchor();
                return true;
            case R.id.action_purge:
                mPresenter.purgeAnchor();
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

        if (!mPresenter.isDeleted(mIntentAnchorId)) {
            fabActionEditMode();
        } else {
            fabActionRestoreMode();
        }

        mBtnNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.navigateToPlace(mIntentAnchorId);
            }
        });
    }

    private void fabActionEditMode() {
        mFabActionAnchor.setImageDrawable(getDrawable(R.drawable.ic_create_black_24dp));
        mFabActionAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.editAnchor();
            }
        });
        mLinearLayoutDeletedDate.setVisibility(View.GONE);
        mLinearLayoutButtons.setVisibility(View.VISIBLE);
    }

    private void fabActionRestoreMode() {
        mFabActionAnchor.setImageDrawable(getDrawable(R.drawable.ic_refresh_black_24dp));
        mFabActionAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.restoreAnchor();
            }
        });
        mLinearLayoutDeletedDate.setVisibility(View.VISIBLE);
        mLinearLayoutButtons.setVisibility(View.GONE);
    }

    private void fillData(Anchor anchor) {
        if (anchor != null) {
            mCollapsingToolbar.setTitle(anchor.getTitle());
            mTextViewDeletedDate.setText(mContext.getString(R.string.msg_deleted_on,
                    SystemUtils.getTime(mContext, anchor.getDeletedTimestamp()),
                    SystemUtils.getDate(mContext, anchor.getDeletedTimestamp())));
            mTextViewDescription.setText(anchor.getDescription());
            mTextViewLatLng.setText(anchor.getLatitude() + ", " + anchor.getLongitude());
            if (anchor.isReminder()) {
                mTextViewReminder.setText(R.string.msg_reminder_location_enabled);

                mImageViewReminderIcon.setImageDrawable(SystemUtils.getDrawable(
                        mContext, R.drawable.ic_notifications_black_24dp, R.color.blue500));

            } else {
                mTextViewReminder.setText(R.string.msg_reminder_location_disabled);
                mImageViewReminderIcon.setImageDrawable(SystemUtils.getDrawable(
                        mContext, R.drawable.ic_notifications_off_black_24dp, R.color.grey700));
            }
            SystemUtils.tintDrawable(mImageViewColorIcon.getDrawable(), mContext, anchor.getColor());
        }
    }

    private void tintElementsWithAnchorColor(int color) {
        mCollapsingToolbar.setBackgroundColor(color);
        mFabActionAnchor.setBackgroundTintList(ColorStateList.valueOf(color));
        mToolbarMarkerIcon.setImageTintList(ColorStateList.valueOf(color));
    }

    //endregion
}
