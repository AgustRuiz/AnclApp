package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.presenter.NewAnchorPresenter;
import es.agustruiz.anclapp.ui.anchor.utils.ColorDialogAppCompatActivity;
import es.agustruiz.anclapp.ui.anchor.utils.GetBitmapFromUrlTask;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class NewAnchorActivity extends AppCompatActivity implements ColorDialogAppCompatActivity {

    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    private final int HEADER_TRANSITION_DURATION = 500;
    public static final String LATITUDE_INTENT_TAG = "latitudeIntentTag";
    public static final String LONGITUDE_INTENT_TAG = "longitudeIntentTag";
    public static final String DESCRIPTION_INTENT_TAG = "descriptionIntentTag";

    private final String COLOR_DIALOG_TAG = "dialog";

    Context mContext;
    NewAnchorPresenter mPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @Bind(R.id.toolbar_save_button)
    Button mBtnSaveAnchor;

    @Bind(R.id.toolbar_marker_icon)
    ImageView mToolbarMarkerIcon;

    @Bind(R.id.toolbar_title)
    EditText mEditTextTitle;

    @Bind(R.id.new_anchor_description)
    EditText mEditTextDescription;

    @Bind(R.id.new_anchor_lat_lng)
    TextView mLatLngText;

    @Bind(R.id.new_anchor_tag)
    EditText mEditTextTag;

    @Bind(R.id.new_anchor_reminder_value)
    SwitchCompat mSwitchReminder;

    @Bind(R.id.new_anchor_color_button)
    LinearLayoutCompat mBtnColorSelection;

    @Bind(R.id.new_anchor_color_icon)
    ImageView mAnchorColorIcon;

    @Bind(R.id.new_anchor_color_text)
    TextView mAnchorColorText;

    String mSelectedColorValue = null;
    public static final String SELECTED_COLOR_VALUE_TAG = "mSelectedColorValue";
    String mSelectedColorTitle = null;
    public static final String SELECTED_COLOR_TITLE_TAG = "mSelectedColorTitle";

    Double mIntentLatitude = null;
    Double mIntentLongitude = null;
    String mIntentDescription = null;

    boolean isHeaderLoaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anchor);
        ButterKnife.bind(this);
        mPresenter = new NewAnchorPresenter(this);
        mContext = getApplicationContext();
        getIntentExtras(getIntent());
        initializeViews(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SELECTED_COLOR_VALUE_TAG, mSelectedColorValue);
        outState.putString(SELECTED_COLOR_TITLE_TAG, mSelectedColorTitle);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!isHeaderLoaded) {
            getMapHeaderImage(mIntentLatitude, mIntentLongitude);
        }
        tintElementsWithAnchorColor();
    }

    //region [Public methods]

    public void showMessageView(String message) {
        Snackbar.make(mToolbarLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public Double getLatitudeValue() {
        return mIntentLatitude;
    }

    public Double getLongitudeValue() {
        return mIntentLongitude;
    }

    public String getTitleValue() {
        return mEditTextTitle.getText().toString();
    }

    public String getDescriptionValue() {
        return mEditTextDescription.getText().toString();
    }

    public String getColorValue() {
        return mSelectedColorValue;
    }

    public boolean getReminderValue() {
        return mSwitchReminder.isChecked();
    }

    //endregion

    //region [Private methods]

    private void getIntentExtras(Intent intent) {
        mIntentLatitude = intent.getDoubleExtra(LATITUDE_INTENT_TAG, 0);
        mIntentLongitude = intent.getDoubleExtra(LONGITUDE_INTENT_TAG, 0);
        mIntentDescription = intent.getStringExtra(DESCRIPTION_INTENT_TAG);
    }

    private void initializeViews(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditTextDescription.setText(mIntentDescription);
        mLatLngText.setText(mIntentLatitude.toString() + ", " + mIntentLongitude.toString());
        mSwitchReminder.setChecked(preferences.getBoolean(
                getString(R.string.key_pref_location_reminder),
                false
        ));

        if (savedInstanceState != null) {
            mSelectedColorValue = savedInstanceState.getString(SELECTED_COLOR_VALUE_TAG);
            mSelectedColorTitle = savedInstanceState.getString(SELECTED_COLOR_TITLE_TAG);
        } else {
            mSelectedColorValue = preferences.getString(
                    getString(R.string.key_pref_anchors_color),
                    getString(R.string.pref_anchors_color_default_value));
            mSelectedColorTitle = getString(R.string.default_color);
        }

        setAnchorColorValues(mSelectedColorTitle, mSelectedColorValue);

        mBtnColorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog();
            }
        });

        mBtnSaveAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveNewAnchor();
            }
        });
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
                    Bitmap mBitmapHeader = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
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
                    isHeaderLoaded = true;
                }
            }
        });
        getHeaderTask.execute(urlString);
    }

    private void tintElementsWithAnchorColor() {
        int color = Color.parseColor(mSelectedColorValue);
        if (!isHeaderLoaded) {
            mToolbarLayout.setBackgroundColor(color);
        }
        mToolbarMarkerIcon.setImageTintList(ColorStateList.valueOf(color));
    }

    //endregion

    //region [ColorDialogAppCompatActivity methods]

    @Override
    public void showColorDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(COLOR_DIALOG_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment newFragment = ColorDialogFragment.newInstance(mSelectedColorValue, mSelectedColorTitle);
        newFragment.show(fragmentTransaction, COLOR_DIALOG_TAG);
    }

    @Override
    public void setAnchorColorValues(String title, String entryValue) {
        mSelectedColorTitle = title;
        mSelectedColorValue = entryValue;
        mAnchorColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(entryValue)));
        mAnchorColorText.setText(mSelectedColorTitle);
        tintElementsWithAnchorColor();
    }

    //endregion
}
