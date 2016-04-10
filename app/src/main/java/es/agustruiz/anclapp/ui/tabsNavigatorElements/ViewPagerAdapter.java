package es.agustruiz.anclapp.ui.tabsNavigatorElements;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;
import es.agustruiz.anclapp.ui.fragment.AnchorListFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {

    CharSequence Titles[]; // This will Store the Titles of the Tabs which are Going to be passed when ViewPagerAdapter is created
    int NumbOfTabs; // Store the number of tabs, this will also be passed when the ViewPagerAdapter is created

    GoogleMapFragment mGoogleMapFragment;
    AnchorListFragment mAnchorListFragment;


    // Build a Constructor and assign the passed Values to appropriate values in the class
    public ViewPagerAdapter(FragmentManager fm, CharSequence mTitles[], int mNumbOfTabsumb) {
        super(fm);
        this.Titles = mTitles;
        this.NumbOfTabs = mNumbOfTabsumb;
    }

    //This method return the fragment for the every position in the View Pager
    @Override
    public Fragment getItem(int position) {
        Fragment returnFragment = null;
        switch (position) {
            case 0:
                returnFragment = mGoogleMapFragment =
                        (mGoogleMapFragment == null ? new GoogleMapFragment() : mGoogleMapFragment);
                break;
            case 1:
                returnFragment = mAnchorListFragment =
                        (mAnchorListFragment == null ? new AnchorListFragment() : mAnchorListFragment);
                break;
        }
        return returnFragment;
    }

    // This method return the titles for the Tabs in the Tab Strip

    @Override
    public CharSequence getPageTitle(int position) {
        return Titles[position];
    }

    // This method return the Number of tabs for the tabs Strip

    @Override
    public int getCount() {
        return NumbOfTabs;
    }
}