package es.agustruiz.anclapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.presenter.MainActivityPresenter;
import es.agustruiz.anclapp.ui.customView.CustomViewPager;
import es.agustruiz.anclapp.ui.fragment.AnchorListFragment;
import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName() + "[A]";

    //region [Binded views and variables]

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.fab_add_anchor)
    FloatingActionButton mFabAddAnchor;

    @Bind(R.id.fab_center_view)
    FloatingActionButton mFabCenterView;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.tabs)
    TabLayout mTabLayout;
    CharSequence tabTitles[] = {"Map", "Anchors"};
    int tabNumbOfTabs = tabTitles.length;
    public final int TAB_MAP = 0;
    public final int TAB_LIST = 1;
    int tabSelected = TAB_MAP;

    @Bind(R.id.pager)
    CustomViewPager mCustomViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Bind(R.id.card_view)
    CardView mCardView;
    boolean isCardViewShown = false;
    final String IS_CARD_VIEW_SHOWN = "isCardViewShown";

    @Bind(R.id.fab_card_view_dismiss)
    FloatingActionButton mFabDismissCardView;

    @Bind(R.id.card_view_address)
    TextView cardViewAddress;
    final String CARD_VIEW_ADDRESS = "cardViewAddress";

    @Bind(R.id.card_view_locality)
    TextView cardViewLocality;
    final String CARD_VIEW_LOCALITY = "cardViewLocality";

    @Bind(R.id.card_view_distance)
    TextView cardViewDistance;
    final String CARD_VIEW_DISTANCE = "cardViewDistance";

    boolean isAutoCenterMap = false; // TODO Consider change it to a shared prefference
    final String IS_AUTO_CENTER_MAP_TAG = "isAutoCenterMap";
    final int ANIMATION_DURATION = 200;

    Context mContext;
    MainActivityPresenter mPresenter;

    //endregion

    //region [Activity methods]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        mPresenter = new MainActivityPresenter(this);
        initializeSavedInstanceState(savedInstanceState);
        initializeViews();
        initializeTabNavigation();
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        View outView = super.onCreateView(parent, name, context, attrs);
        if (isCardViewShown) {
            showLocationCardView();
            showFabDismissCardView();
            hideFabCenterView();
        }
        return outView;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(IS_AUTO_CENTER_MAP_TAG, isAutoCenterMap);
        outState.putBoolean(IS_CARD_VIEW_SHOWN, isCardViewShown);
        outState.putCharSequence(CARD_VIEW_ADDRESS, cardViewAddress.getText());
        outState.putCharSequence(CARD_VIEW_LOCALITY, cardViewLocality.getText());
        outState.putCharSequence(CARD_VIEW_DISTANCE, cardViewDistance.getText());
    }

    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region [Public methods]

    public void showMessageView(View view, String message) {
        view = (view != null ? view : mFabCenterView);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public void showMessageView(String s) {
        showMessageView(null, s);
    }

    public void showFabCenterView() {
        if (tabSelected == TAB_MAP) {
            mFabCenterView.show();
        }
    }

    public void hideFabCenterView() {
        mFabCenterView.hide();
    }

    public void showFabDismissCardView() {
        mFabDismissCardView.show();
    }

    public void hideFabDismissCardView() {
        mFabDismissCardView.hide();
    }

    public void setFabCenterViewState(boolean value) {
        isAutoCenterMap = value;
        if (isAutoCenterMap) {
            mFabCenterView.getDrawable()
                    .setTint(getResources().getColor(R.color.blue500, getTheme()));
        } else {
            mFabCenterView.getDrawable()
                    .setTint(getResources().getColor(R.color.grey700, getTheme()));
        }
    }

    public void showLocationCardView() {
        final int startHeight = mCardView.getHeight();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                SystemUtils.getDevideWidth(mCardView.getContext()), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mCardView.measure(widthMeasureSpec, heightMeasureSpec);
        final int endHeight = mCardView.getMeasuredHeight();
        final int diffHeight = endHeight - startHeight;
        final ViewGroup.LayoutParams params = mCardView.getLayoutParams();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.height = (int) (startHeight + (diffHeight * interpolatedTime));
                mCardView.setLayoutParams(params);
            }
        };
        animation.setDuration(ANIMATION_DURATION);
        mCardView.startAnimation(animation);
        isCardViewShown = true;
    }

    public void hideLocationCardView() {
        final ViewGroup.LayoutParams params = mCardView.getLayoutParams();
        final int maxHeight = params.height;
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewGroup.LayoutParams params = mCardView.getLayoutParams();
                params.height = (int) (maxHeight - (maxHeight * interpolatedTime));
                mCardView.setLayoutParams(params);/**/
            }
        };
        animation.setDuration(ANIMATION_DURATION);
        mCardView.startAnimation(animation);
        isCardViewShown = false;
    }

    public void fillLocationCardView(String address, String locality, String distance) {
        cardViewAddress.setText(address);
        cardViewLocality.setText(locality);
        cardViewDistance.setText(distance);
    }

    public int getTabSelected() {
        return tabSelected;
    }

    //endregion

    //region [Private methods]

    private void initializeSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            isAutoCenterMap = savedInstanceState.getBoolean(IS_AUTO_CENTER_MAP_TAG);
            setFabCenterViewState(isAutoCenterMap);
            isCardViewShown = savedInstanceState.getBoolean(IS_CARD_VIEW_SHOWN);
            cardViewAddress.setText(savedInstanceState.getCharSequence(CARD_VIEW_ADDRESS));
            cardViewLocality.setText(savedInstanceState.getCharSequence(CARD_VIEW_LOCALITY));
            cardViewDistance.setText(savedInstanceState.getCharSequence(CARD_VIEW_DISTANCE));
        }
    }

    private void initializeViews() {
        setSupportActionBar(mToolbar);
        mFabAddAnchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPresenter.addAnchor();
            }
        });
        mFabCenterView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isAutoCenterMap = !isAutoCenterMap;
                setFabCenterViewState(isAutoCenterMap);
                mPresenter.centerMapOnCurrentLocation(isAutoCenterMap);
            }
        });
        mFabDismissCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancelMarker();
            }
        });
    }

    private void initializeTabNavigation() {
        mCustomViewPager.setPagingEnabled(false);
        mCustomViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tabSelected = position;
                if (position == 0 && !isCardViewShown) {
                    showFabCenterView();
                } else {
                    hideFabCenterView();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mCustomViewPager.setAdapter(mSectionsPagerAdapter);
        mTabLayout.setupWithViewPager(mCustomViewPager);
        mTabLayout.setSelectedTabIndicatorColor(getColor(R.color.tabsScrollColor));
        mTabLayout.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
    }

    //endregion

    //region [PlaceholderFragment]

    private static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static Fragment newInstance(int sectionNumber) {
            Fragment fragment = null;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            switch (sectionNumber) {
                case 0:
                    fragment = new GoogleMapFragment();
                    break;
                case 1:
                    fragment = new AnchorListFragment();
                    break;
            }
            if (fragment != null)
                fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = null;
            int sectionNumber = getArguments().getInt(ARG_SECTION_NUMBER);
            switch (sectionNumber) {
                case 0:
                    rootView = inflater.inflate(R.layout.fragment_map, container, false);
                    break;
                case 1:
                    rootView = inflater.inflate(R.layout.fragment_anchor_list, container, false);
                    break;
            }
            return rootView;
        }
    }

    //endregion

    //region [SectionsPagerAdapter]

    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return tabNumbOfTabs;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            try {
                return tabTitles[position];
            } catch (Exception ignored) {
            }
            return null;
        }
    }

    //endregion

}
