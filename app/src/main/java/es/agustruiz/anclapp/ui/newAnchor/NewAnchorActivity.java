package es.agustruiz.anclapp.ui.newAnchor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;

public class NewAnchorActivity extends AppCompatActivity {

    Context mContext = null;
    android.support.v7.app.ActionBar mActionBar = null;

    @Bind(R.id.new_anchor_title)
    EditText mTitle;

    @Bind(R.id.new_anchor_description)
    EditText mDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mTitle.clearFocus();
    }



}
