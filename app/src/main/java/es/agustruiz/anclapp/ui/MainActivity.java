package es.agustruiz.anclapp.ui;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
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

    ViewPagerAdapter mViewPagerAdapter;
    CharSequence tabTitles[] = {"Map", "Anchors"};
    int tabNumbOfTabs = tabTitles.length;

    ActionBarDrawerToggle mDrawerToggle;

    MainActivityPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mPresenter = new MainActivityPresenter(this);

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
            public void onClick(View view) { mPresenter.centerViewOnLocation();
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
                if (position == 0) {
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

    //region [Public methods]

    // TODO consider change the duration by message length
    public void showMessageView(View view, String message) {
        view = (view != null ? view : mFabCenterView);
        Snackbar.make(view, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .show();
    }

    public void showFabCenterView() {
        mFabCenterView.show();
    }

    public void hideFabCenterView() {
        mFabCenterView.hide();
    }

    public void centerMapView(Location location){
        if(location!=null){

        }
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

        switch (id){
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
