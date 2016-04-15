package es.agustruiz.anclapp.ui;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.presenter.MainActivityPresenter;
import es.agustruiz.anclapp.ui.tabsNavigatorElements.SlidingTabLayout;
import es.agustruiz.anclapp.ui.tabsNavigatorElements.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String LOG_TAG = MainActivity.class.getName() + "[A]";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.fab_add_anchor)
    FloatingActionButton mFabAddAnchor;

    @Bind(R.id.fab_center_view)
    FloatingActionButton mFabCenterView;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawer;

    @Bind(R.id.tabs)
    SlidingTabLayout mSlidingTabLayout;

    @Bind(R.id.pager)
    ViewPager mViewPager;

    @Bind(R.id.nav_view)
    NavigationView mNavigationView;

    @Bind(R.id.card_view)
    CardView cardView;
    boolean isCardViewShown = false;
    final String IS_CARD_VIEW_SHOWN = "isCardViewShown";

    @Bind(R.id.btn_cancel_marker)
    Button mCancelMarker;

    @Bind(R.id.card_view_address)
    TextView cardViewAddress;
    final String CARD_VIEW_ADDRESS = "cardViewAddress";

    @Bind(R.id.card_view_locality)
    TextView cardViewLocality;
    final String CARD_VIEW_LOCALITY = "cardViewLocality";

    @Bind(R.id.card_view_distance)
    TextView cardViewDistance;
    final String CARD_VIEW_DISTANCE = "cardViewDistance";

    ViewPagerAdapter mViewPagerAdapter;
    CharSequence tabTitles[] = {"Map", "Anchors"};
    int tabNumbOfTabs = tabTitles.length;
    ActionBarDrawerToggle mDrawerToggle;

    public final int TAB_MAP = 0;
    public final int TAB_LIST = 1;
    int tabSelected = TAB_MAP;

    boolean isAutoCenterMap = false; // TODO Consider change it to a shared prefference
    final String IS_AUTO_CENTER_MAP_TAG = "isAutoCenterMap";
    final int ANIMATION_DURATION = 200;

    MainActivityPresenter mPresenter;

    //region [Activity methods]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainActivityPresenter(this);

        //region [Saved state]

        if (savedInstanceState != null) {
            isAutoCenterMap = savedInstanceState.getBoolean(IS_AUTO_CENTER_MAP_TAG);
            setAutoCenterMap(isAutoCenterMap);
            isCardViewShown = savedInstanceState.getBoolean(IS_CARD_VIEW_SHOWN);
            if (isCardViewShown) {
                showLocationCardView();
                hideFabCenterView();
            }
            cardViewAddress.setText(savedInstanceState.getCharSequence(CARD_VIEW_ADDRESS));
            cardViewLocality.setText(savedInstanceState.getCharSequence(CARD_VIEW_LOCALITY));
            cardViewDistance.setText(savedInstanceState.getCharSequence(CARD_VIEW_DISTANCE));
        }

        //endregion

        //region [Layout views]

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
                setAutoCenterMap(!isAutoCenterMap);
                mPresenter.centerMapOnCurrentLocation();
            }
        });

        mCancelMarker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.cancelMarker();
            }
        });

        //endregion

        //region [Sidedrawer]

        mDrawerToggle = new ActionBarDrawerToggle(
                this, mDrawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        mNavigationView.setNavigationItemSelectedListener(this);

        //endregion

        //region [Tab navigator]

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), tabTitles, tabNumbOfTabs);
        mViewPager.setAdapter(mViewPagerAdapter);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabSelected = position;
                if (position == 0 && !isLocationCardViewShown()) {
                    showFabCenterView();
                } else {
                    hideFabCenterView();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //endregion

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

    //endregion

    //region [Public methods]

    // TODO consider change the duration by message length
    public void showMessageView(View view, String message) {
        view = (view != null ? view : mFabCenterView);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public void showFabCenterView() {
        if (tabSelected == TAB_MAP) {
            mFabCenterView.show();
        }
    }

    public void hideFabCenterView() {
        mFabCenterView.hide();
    }

    public void setAutoCenterMap(boolean value) {
        isAutoCenterMap = value;
        if (isAutoCenterMap) {
            mFabCenterView.getDrawable()
                    .setTint(getResources().getColor(R.color.blue500, getTheme()));
        } else {
            mFabCenterView.getDrawable()
                    .setTint(getResources().getColor(R.color.grey700, getTheme()));
        }
    }

    public boolean isAutoCenterMap() {
        return isAutoCenterMap;
    }

    public void showLocationCardView() {
        final int startHeight = cardView.getHeight();
        int widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(
                SystemUtils.getDevideWidth(cardView.getContext()), View.MeasureSpec.AT_MOST);
        int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        cardView.measure(widthMeasureSpec, heightMeasureSpec);
        final int endHeight = cardView.getMeasuredHeight();
        final int diffHeight = endHeight - startHeight;
        final ViewGroup.LayoutParams params = cardView.getLayoutParams();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                params.height = (int) (startHeight + (diffHeight * interpolatedTime));
                cardView.setLayoutParams(params);
            }
        };
        animation.setDuration(ANIMATION_DURATION);
        cardView.startAnimation(animation);
        isCardViewShown = true;
    }

    public void hideLocationCardView() {
        final ViewGroup.LayoutParams params = cardView.getLayoutParams();
        final int maxHeight = params.height;
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                ViewGroup.LayoutParams params = cardView.getLayoutParams();
                params.height = (int) (maxHeight - (maxHeight * interpolatedTime));
                cardView.setLayoutParams(params);/**/
            }
        };
        animation.setDuration(ANIMATION_DURATION);
        cardView.startAnimation(animation);
        isCardViewShown = false;
    }

    public boolean isLocationCardViewShown() {
        return isCardViewShown;
    }

    public int getTabSelected() {
        return tabSelected;
    }

    public void fillLocationCardView(String address, String locality, String distance) {
        cardViewAddress.setText(address);
        cardViewLocality.setText(locality);
        cardViewDistance.setText(distance);
    }

    //endregion

    //region [Activity methods]

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

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region [NavigationView.OnNavigationItemSelectedListener]

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_camera:
                break;
            case R.id.nav_gallery:
                break;
            case R.id.nav_slideshow:
                break;
            case R.id.nav_manage:
                break;
            case R.id.nav_share:
                break;
            case R.id.nav_send:
                break;
        }

        mDrawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //endregion

}
