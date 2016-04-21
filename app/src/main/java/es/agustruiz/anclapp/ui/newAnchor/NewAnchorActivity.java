package es.agustruiz.anclapp.ui.newAnchor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;

public class NewAnchorActivity extends AppCompatActivity {


    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.toolbar_save_button)
    Button mSave;

    @Bind(R.id.toolbar_title)
    EditText mTitle;

    @Bind(R.id.new_anchor_description)
    EditText mDescription;

    @Bind(R.id.new_anchor_tag)
    EditText mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
