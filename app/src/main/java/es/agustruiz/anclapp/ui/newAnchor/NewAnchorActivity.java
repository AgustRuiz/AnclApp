package es.agustruiz.anclapp.ui.newAnchor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.ui.fragment.ColorDialogFragment;

public class NewAnchorActivity extends AppCompatActivity {


    public final String LOG_TAG = NewAnchorActivity.class.getName() + "[A]";

    Context mContext;

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

    @Bind(R.id.new_anchor_color_button)
    LinearLayoutCompat mColorSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mContext = getApplicationContext();

        mColorSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "Select color!", Toast.LENGTH_SHORT).show();
                showDialog();
            }
        });
    }


    int mStackLevel = -1;
    private void showDialog(){
        mStackLevel++;

        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        // Create and show the dialog.
        DialogFragment newFragment = ColorDialogFragment.newInstance(mStackLevel);
        newFragment.show(ft, "dialog");
    }
}
