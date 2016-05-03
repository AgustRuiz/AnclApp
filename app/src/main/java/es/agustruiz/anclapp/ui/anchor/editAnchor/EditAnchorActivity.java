package es.agustruiz.anclapp.ui.anchor.editAnchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
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
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.EditAnchorPresenter;
import es.agustruiz.anclapp.ui.anchor.ColorDialogAppCompatActivity;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class EditAnchorActivity extends AppCompatActivity implements ColorDialogAppCompatActivity {

    public final String LOG_TAG = EditAnchorActivity.class.getName() + "[A]";

    private final int HEADER_TRANSITION_DURATION = 500;

    public static final String ID_INTENT_TAG = "idIntentTag";
    protected long mIntentAnchorId;

    Context mContext;
    EditAnchorPresenter mPresenter;

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

    private final String COLOR_DIALOG_TAG = "dialog";
    String mSelectedColorValue = null;
    public static final String SELECTED_COLOR_VALUE_TAG = "mSelectedColorValue";
    String mSelectedColorTitle = null;
    public static final String SELECTED_COLOR_TITLE_TAG = "mSelectedColorTitle";

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
        tintElementsWithAnchorColor(
                Color.parseColor(mPresenter.getAnchor(mIntentAnchorId).getColor()));
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
    }

    private void tintElementsWithAnchorColor(int color) {
        ColorStateList colorStateList = ColorStateList.valueOf(color);
        mToolbarLayout.setBackgroundColor(color);
        mToolbarMarkerIcon.setImageTintList(colorStateList);
        mAnchorColorIcon.setImageTintList(colorStateList);
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
        tintElementsWithAnchorColor(Color.parseColor(mSelectedColorValue));
    }

    //endregion
}
