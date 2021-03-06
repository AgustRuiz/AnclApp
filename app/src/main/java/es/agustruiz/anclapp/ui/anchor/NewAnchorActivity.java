package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.presenter.NewAnchorPresenter;
import es.agustruiz.anclapp.service.ShareAnchor;
import es.agustruiz.anclapp.ui.utils.ColorDialogAppCompatActivity;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class NewAnchorActivity extends AppCompatActivity implements ColorDialogAppCompatActivity {

    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    public static final String LATITUDE_INTENT_TAG = "latitudeIntentTag";
    public static final String LONGITUDE_INTENT_TAG = "longitudeIntentTag";
    public static final String DESCRIPTION_INTENT_TAG = "descriptionIntentTag";

    private final String COLOR_DIALOG_TAG = "dialog";

    Context mContext;
    NewAnchorPresenter mPresenter;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @BindView(R.id.toolbar_save_button)
    Button mBtnSaveAnchor;

    @BindView(R.id.toolbar_marker_icon)
    ImageView mToolbarMarkerIcon;

    @BindView(R.id.toolbar_title)
    EditText mEditTextTitle;

    @BindView(R.id.new_anchor_description)
    EditText mEditTextDescription;

    @BindView(R.id.new_anchor_lat_lng)
    TextView mLatLngText;

    @BindView(R.id.new_anchor_tag)
    EditText mEditTextTag;

    @BindView(R.id.new_anchor_reminder_value)
    SwitchCompat mSwitchReminder;

    @BindView(R.id.new_anchor_color_button)
    LinearLayoutCompat mBtnColorSelection;

    @BindView(R.id.new_anchor_color_icon)
    ImageView mAnchorColorIcon;

    @BindView(R.id.new_anchor_color_text)
    TextView mAnchorColorText;

    String mSelectedColorValue = null;
    public static final String SELECTED_COLOR_VALUE_TAG = "mSelectedColorValue";
    String mSelectedColorTitle = null;
    public static final String SELECTED_COLOR_TITLE_TAG = "mSelectedColorTitle";

    Double mIntentLatitude = null;
    Double mIntentLongitude = null;
    String mIntentTitle = null;
    String mIntentDescription = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anchor);
        ButterKnife.bind(this);
        mPresenter = new NewAnchorPresenter(this);
        mContext = getApplicationContext();
        getIntentExtras(getIntent());
        initializeViews(savedInstanceState);
        tintElementsWithAnchorColor();
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
        mPresenter.setHeaderImage();
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

    public void setHeaderBackground(Drawable background){
        mToolbarLayout.setBackground(background);
    }

    public Drawable getHeaderBackground(){
        return mToolbarLayout.getBackground();
    }

    public int getHeaderWidth(){
        return mToolbarLayout.getWidth();
    }

    public int getHeaderHeight(){
        return mToolbarLayout.getHeight();
    }

    //endregion

    //region [Private methods]

    private void getIntentExtras(Intent intent) {
        Uri uri = intent.getData();
        if(uri==null){
            mIntentLatitude = intent.getDoubleExtra(LATITUDE_INTENT_TAG, 0);
            mIntentLongitude = intent.getDoubleExtra(LONGITUDE_INTENT_TAG, 0);
            mIntentTitle = mContext.getString(R.string.new_anchor);
            mIntentDescription = intent.getStringExtra(DESCRIPTION_INTENT_TAG);
        }else{
            //TODO check not valid uri
            mIntentLatitude = Double.parseDouble(uri.getQueryParameter(ShareAnchor.URI_PARAM_LATITUDE));
            mIntentLongitude = Double.parseDouble(uri.getQueryParameter(ShareAnchor.URI_PARAM_LONGITUDE));
            mIntentTitle = uri.getQueryParameter(ShareAnchor.URI_PARAM_TITLE);
            mIntentDescription = uri.getQueryParameter(ShareAnchor.URI_PARAM_DESCRIPTION);
        }
    }

    private void initializeViews(Bundle savedInstanceState) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEditTextTitle.setText(mIntentTitle);
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

    private void tintElementsWithAnchorColor() {
        mAnchorColorIcon.setColorFilter(Color.parseColor(mSelectedColorValue));
        mToolbarLayout.setBackgroundColor(Color.parseColor(mSelectedColorValue));
        mToolbarMarkerIcon.setColorFilter(Color.parseColor(mSelectedColorValue));
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
        mAnchorColorText.setText(mSelectedColorTitle);
        tintElementsWithAnchorColor();
    }

    //endregion
}
