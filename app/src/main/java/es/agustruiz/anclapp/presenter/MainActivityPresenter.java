package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.MainActivity;

public class MainActivityPresenter implements Presenter {

    public static final String LOG_TAG = MainActivityPresenter.class.getName() + "[A]";

    //region [Variables]

    MainActivity mActivity;
    Context mContext;
    EventsUtil eventsUtil = EventsUtil.getInstance();
    Double mIntentLatitude = null;
    Double mIntentLongitude = null;
    String mIntentDescription = null;

    //endregion

    //region [Public methods]

    public MainActivityPresenter(MainActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        registerEvents();
    }

    public void centerMapOnCurrentLocation(boolean state) {
        eventsUtil.centerMapOnCurrentLocationEvent(state);
    }

    public void addAnchor() {
        eventsUtil.addNewAnchorOnMap();
    }

    public void cancelMarker() {
        mActivity.hideLocationCardView();
        mActivity.setFabCenterViewState(false);
        mActivity.showFabCenterView();
        mActivity.hideFabDismissCardView();
        eventsUtil.mapCancelNewMarker();
    }

    //endregion

    //region [Overriden Presenter methods]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(null, message);
    }

    //endregion

    //region [Private methods]

    private void registerEvents() {
        eventsUtil.addEventListener(EventsUtil.GET_NEW_MARKER_DATA, new IEventHandler() {
            @Override
            public void callback(Event event) {
                //String allData = (String) event.getParameter();
                String[] data = (String[]) event.getParameter();
                fillLocationCard(data[0], data[1], data[2]);
                showLocationCard();
            }
        });

        eventsUtil.addEventListener(EventsUtil.HIDE_LOCATION_CARD, new IEventHandler() {
            @Override
            public void callback(Event event) {
                hideLocationCard();
            }
        });

        eventsUtil.addEventListener(EventsUtil.DISMISS_FAB_CENTER_MAP, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mActivity.setFabCenterViewState(false);
            }
        });
    }

    private void fillLocationCard(String address, String locality, String distance) {
        mActivity.fillLocationCardView(address, locality, distance);
    }

    private void hideLocationCard(){
        mActivity.hideLocationCardView();
        mActivity.hideFabDismissCardView();
        if(mActivity.getTabSelected()==mActivity.TAB_MAP){
            mActivity.showFabCenterView();
        }
    }

    private void showLocationCard(){
        mActivity.hideFabCenterView();
        mActivity.showFabDismissCardView();
        mActivity.showLocationCardView();
    }

    //endregion
}
