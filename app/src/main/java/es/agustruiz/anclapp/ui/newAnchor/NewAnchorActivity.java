package es.agustruiz.anclapp.ui.newAnchor;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import es.agustruiz.anclapp.R;

public class NewAnchorActivity extends AppCompatActivity {

    Context mContext = null;
    android.support.v7.app.ActionBar mActionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_anchor);

        mContext = getApplicationContext();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
}
