package es.agustruiz.anclapp.ui.seeAnchor;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.TextureView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;

public class SeeAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = SeeAnchorActivity.class.getName() + "[A]";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;

    @Bind(R.id.toolbar_text_view_title)
    TextView mTextViewTitle;

    @Bind(R.id.see_anchor_description)
    TextView mTextViewDescription;

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
    protected AnchorDAO mAnchorDAO;
    protected Anchor mAnchor = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_anchor);
        ButterKnife.bind(this);
        mContext = getApplicationContext();

        getIntentExtras(getIntent());

        mAnchorDAO = new AnchorDAO(mContext);
        mAnchorDAO.openReadOnly();
        mAnchor = mAnchorDAO.get(mIntentAnchorId);
        mAnchorDAO.close();

        Log.d(LOG_TAG, "Anchor id: " + mIntentAnchorId);

        initialize();

        fillData();

    }

    private void getIntentExtras(Intent intent) {
        mIntentAnchorId = intent.getLongExtra(ANCHOR_ID_INTENT_TAG, Long.MIN_VALUE);
    }

    private void initialize(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void fillData(){
        if(mAnchor!=null){
            mTextViewTitle.setText(mAnchor.getTitle());
            mTextViewDescription.setText(mAnchor.getDescription());
            if(mAnchor.isReminder()){
                mTextViewReminder.setText("Reminder by location enabled");
            }else{
                mTextViewReminder.setText("Reminder by location disabled");
                mImageViewReminderIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_notifications_off_black_24dp, getTheme()));
            }
            mImageViewColorIcon.setImageTintList(ColorStateList.valueOf(Color.parseColor(mAnchor.getColor())));
        }
    }
}
