package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.EditAnchorPresenter;
import es.agustruiz.anclapp.ui.anchor.utils.ColorDialogAppCompatActivity;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class EditAnchorActivity extends AppCompatActivity implements ColorDialogAppCompatActivity {

    public final String LOG_TAG = EditAnchorActivity.class.getName() + "[A]";

    public static final String ID_INTENT_TAG = "idIntentTag";
    protected long mIntentAnchorId;

    Context mContext;
    EditAnchorPresenter mPresenter;

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

    private final String COLOR_DIALOG_TAG = "dialog";
    String mSelectedColorValue = null;
    String mSelectedColorTitle = null;

    //region [Activity methods]

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_anchor);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mPresenter = new EditAnchorPresenter(this);
        getIntentExtras(getIntent());
        initializeViews(mPresenter.getAnchor(mIntentAnchorId));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mPresenter.setHeaderImage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region [Public methods]

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

    public String getAnchorTitle(){
        return mEditTextTitle.getText().toString();
    }

    public String getAnchorDescription(){
        return mEditTextDescription.getText().toString();
    }

    public String getAnchorColor(){
        return mSelectedColorValue;
    }

    public boolean isReminder(){
        return mSwitchReminder.isChecked();
    }

    public void showMessageView(String message) {
        Snackbar.make(mToolbarLayout, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    //endregion

    //region [Private methods]

    private void getIntentExtras(Intent intent) {
        mIntentAnchorId = intent.getLongExtra(ID_INTENT_TAG, Long.MIN_VALUE);
    }

    private void initializeViews(Anchor anchor) {
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEditTextTitle.setText(anchor.getTitle());
        mEditTextDescription.setText(anchor.getDescription());
        mLatLngText.setText(anchor.getLatitude() + ", " + anchor.getLongitude());
        mSwitchReminder.setChecked(anchor.isReminder());
        mSelectedColorValue = mPresenter.getAnchor(mIntentAnchorId).getColor();
        mBtnColorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showColorDialog();
            }
        });
        mSelectedColorTitle = ColorDialogFragment.getColorTitle(mContext, mSelectedColorValue);
        setAnchorColorValues(mSelectedColorTitle, mSelectedColorValue);
        tintElementsWithAnchorColor(mPresenter.getAnchor(mIntentAnchorId).getColor());
        mBtnSaveAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.updateAnchor();
            }
        });
    }

    private void tintElementsWithAnchorColor(String colorString) {
        int colorValue = Color.parseColor(colorString);
        mToolbarLayout.setBackgroundColor(colorValue);
        mToolbarMarkerIcon.setColorFilter(Color.parseColor(colorString));
        mAnchorColorIcon.setColorFilter(Color.parseColor(colorString));
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

    //endregion

    @Override
    public void setAnchorColorValues(String title, String entryValue) {
        mSelectedColorTitle = title;
        mSelectedColorValue = entryValue;
        mAnchorColorText.setText(mSelectedColorTitle);
        mAnchorColorIcon.setColorFilter(Color.parseColor(entryValue));
        mToolbarMarkerIcon.setColorFilter(Color.parseColor(entryValue));
    }
}
