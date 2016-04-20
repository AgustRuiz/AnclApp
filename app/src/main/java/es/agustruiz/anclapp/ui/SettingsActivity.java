package es.agustruiz.anclapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.widget.ImageView;
import android.widget.Toast;

import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;

import static es.agustruiz.anclapp.R.xml.preferences;

public class SettingsActivity extends AppCompatPreferenceActivity {

    Context mContext = null;
    PreferenceFragment mPreferenceFragment = null;
    android.support.v7.app.ActionBar mActionBar = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);

        mContext = getApplicationContext();

        mPreferenceFragment = new MyPreferenceFragment();
        getFragmentManager()
                .beginTransaction()
                .replace(android.R.id.content, mPreferenceFragment)
                .commit();

        mActionBar = getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    protected static class MyPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(preferences);
        }
    }
}
