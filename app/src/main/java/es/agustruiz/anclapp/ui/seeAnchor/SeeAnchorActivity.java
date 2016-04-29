package es.agustruiz.anclapp.ui.seeAnchor;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;

public class SeeAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = SeeAnchorActivity.class.getName() + "[A]";

    protected long mIntentAnchorId;
    public static final String ANCHOR_ID_INTENT_TAG = "mIntentAnchorId";

    protected Context mContext;
    protected AnchorDAO mAnchorDAO;
    protected Anchor mAnchor;


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
        Log.d(LOG_TAG, "Anchor: " + mAnchor);
    }

    private void getIntentExtras(Intent intent) {
        mIntentAnchorId = intent.getLongExtra(ANCHOR_ID_INTENT_TAG, Long.MIN_VALUE);
    }
}
