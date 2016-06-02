package es.agustruiz.anclapp.presenter;

import android.content.Context;

import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.activity.MainActivity;

public class MainActivityPresenter implements IPresenter {

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

    //region [Overriden IPresenter methods]

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
