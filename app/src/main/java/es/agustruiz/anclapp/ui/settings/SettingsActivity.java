package es.agustruiz.anclapp.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.MenuItem;

import butterknife.ButterKnife;
import es.agustruiz.anclapp.ui.settings.AppCompatPreferenceActivity;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
