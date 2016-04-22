package es.agustruiz.anclapp.ui.newAnchor;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class NewAnchorActivity extends AppCompatActivity {

    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    private final String COLOR_DIALOG_TAG = "dialog";

    Context mContext;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_save_button)
    Button mSave;

    @Bind(R.id.toolbar_title)
    EditText mTextViewTitle;

    @Bind(R.id.new_anchor_description)
    EditText mTextViewDescription;

    @Bind(R.id.new_anchor_tag)
    EditText mTextViewTag;

    @Bind(R.id.new_anchor_color_button)
    LinearLayoutCompat mBtnColorSelection;

    @Bind(R.id.new_anchor_color_icon)
    ImageView mAnchorColorIcon;

    @Bind(R.id.new_anchor_color_text)
    TextView mAnchorColorText;

    String mSelectedColorValue = null;
    String mSelectedColorTitle = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        mSelectedColorValue = PreferenceManager.getDefaultSharedPreferences(mContext).getString(
                getString(R.string.key_pref_anchors_color),
                getString(R.string.pref_anchors_color_default_value)
        );
        mSelectedColorTitle = getString(R.string.default_color);
        setAnchorColorValues(mSelectedColorTitle, mSelectedColorValue);

        mBtnColorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
    }


    private void showDialog() {
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(COLOR_DIALOG_TAG);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ColorDialogFragment.newInstance(mSelectedColorValue);
        newFragment.show(fragmentTransaction, COLOR_DIALOG_TAG);
    }

    public void setAnchorColorValues(String title, String entryValue) {
        mSelectedColorTitle = title;
        mSelectedColorValue = entryValue;
        mAnchorColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(entryValue)));
        mAnchorColorText.setText(mSelectedColorTitle);
    }
}
