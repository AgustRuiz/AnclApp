package es.agustruiz.anclapp.ui.anchor.newAnchor;

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
import es.agustruiz.anclapp.ui.anchor.GetBitmapFromUrlTask;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class NewAnchorActivity extends AppCompatActivity {

    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    private final int TRIM_MAP_MARGIN = 10;
    private final int MAX_GOOGLE_STATIC_MAP = 640;
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

    @Bind(R.id.toolbar_title)
    EditText mTextViewTitle;

    @Bind(R.id.new_anchor_description)
    EditText mTextViewDescription;

    @Bind(R.id.new_anchor_tag)
    EditText mTextViewTag;

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
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);

        mPresenter = new NewAnchorPresenter(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        getIntentExtras(getIntent());

        initializeViews(savedInstanceState);

        mBtnColorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        mBtnSaveAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.saveNewAnchor();
            }
        });
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
        getMapHeaderImage(mIntentLatitude, mIntentLongitude);
    }

    //region [Public methods]

    public void setAnchorColorValues(String title, String entryValue) {
        mSelectedColorTitle = title;
        mSelectedColorValue = entryValue;
        mAnchorColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(entryValue)));
        mAnchorColorText.setText(mSelectedColorTitle);
    }

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
        return mTextViewTitle.getText().toString();
    }

    public String getDescriptionValue() {
        return mTextViewDescription.getText().toString();
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
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mTextViewDescription.setText(mIntentDescription);
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
    }

    private void showDialog() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(COLOR_DIALOG_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        DialogFragment newFragment = ColorDialogFragment.newInstance(mSelectedColorValue, mSelectedColorTitle);
        newFragment.show(fragmentTransaction, COLOR_DIALOG_TAG);
    }

    private void getMapHeaderImage(Double latitude, Double longitude) {

        // Note: Added a margin on top and bottom to trim Google logo and keep map in center

        final int width = mToolbarLayout.getWidth();
        final int height = mToolbarLayout.getHeight();

        int scaleWidthFactor = (int) Math.ceil((float) width / (float) MAX_GOOGLE_STATIC_MAP);
        int scaleHeightFactor = (int) Math.ceil((float) height / (float) MAX_GOOGLE_STATIC_MAP);
        final int scaleFactor = Math.max(scaleWidthFactor, scaleHeightFactor) > 0 ? Math.max(scaleWidthFactor, scaleHeightFactor) : 1;

        int scaledWidth = (int) Math.ceil(width / scaleFactor);
        int scaledHeight = (int) Math.ceil(height / scaleFactor) + TRIM_MAP_MARGIN * scaleFactor * 2;

        int zoom = 16;

        String urlString = "http://maps.google.com/maps/api/staticmap?center=" + latitude.toString() + ","
                + longitude.toString() + "&zoom=" + zoom + "&size=" + scaledWidth + "x"
                + scaledHeight + "&scale=" + scaleFactor + "&sensor=false";
        GetBitmapFromUrlTask getHeaderTask = new GetBitmapFromUrlTask();
        getHeaderTask.setBitmapFromUrlListener(new GetBitmapFromUrlTask.OnBitmapFromUrlListener() {
            @Override
            public void onBitmapReady(Bitmap bitmap) {
                if (bitmap != null) {
                    Bitmap bitmapCropped = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    Paint p = new Paint();
                    p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                    Canvas c = new Canvas(bitmapCropped);
                    c.drawRect(0, TRIM_MAP_MARGIN * scaleFactor, 0, TRIM_MAP_MARGIN * scaleFactor, p);
                    c.drawBitmap(bitmap, 0, 0, null);
                    mToolbarLayout.setBackground(new BitmapDrawable(getResources(), bitmapCropped));
                }
            }
        });
        getHeaderTask.execute(urlString);
    }

    //endregion
}
