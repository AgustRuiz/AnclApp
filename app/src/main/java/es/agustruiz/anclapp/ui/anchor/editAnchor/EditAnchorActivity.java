package es.agustruiz.anclapp.ui.anchor.editAnchor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.presenter.EditAnchorPresenter;

public class EditAnchorActivity extends AppCompatActivity {

    public final String LOG_TAG = EditAnchorActivity.class.getName() + "[A]";

    private final int HEADER_TRANSITION_DURATION = 500;

    public static final String ID_INTENT_TAG = "idIntentTag";
    protected long mIntentAnchorId;

    Context mContext;
    EditAnchorPresenter mPresenter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    //region [Activity methods]

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mPresenter = new EditAnchorPresenter(this);
        getIntentExtras(getIntent());
        initializeViews();
    }

    //endregion

    //region [Private methods]

    private void getIntentExtras(Intent intent){
        mIntentAnchorId = intent.getLongExtra(ID_INTENT_TAG, Long.MIN_VALUE);
    }

    private void initializeViews(){
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    //endregion
}
