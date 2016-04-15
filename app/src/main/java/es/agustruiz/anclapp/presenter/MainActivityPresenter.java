package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.MainActivity;

public class MainActivityPresenter implements Presenter{

    MainActivity mActivity;
    Context mContext;
    EventsUtil eventsUtil = EventsUtil.getInstance();

    //region [Public methods]

    public MainActivityPresenter(MainActivity activity){
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        eventsUtil.addEventListener(EventsUtil.MAP_LONG_PRESS, new IEventHandler() {
            @Override
            public void callback(Event event) {
                if(!mActivity.isLocationCardViewShown()){
                    LatLng latLng = (LatLng) event.getParameter();
                    mActivity.showLocationCardView();
                    mActivity.hideFabCenterView();
                }
            }
        });

    }

    public void centerMapOnCurrentLocation() {
        EventsUtil.getInstance().centerMapOnLocationEvent(mActivity.isAutoCenterMap());
    }

    public void addAnchor(){
        showMessage("Add anchor here");
    }

    public void cancelMarker() {
        mActivity.hideLocationCardView();
        mActivity.setAutoCenterMap(false);
        mActivity.showFabCenterView();
        EventsUtil.getInstance().cancelNewMarker();
    }

    //endregion

    //region [Presenter]

    @Override
    public void showMessage(View view, String message) {
        mActivity.showMessageView(view, message);
    }

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(null, message);
    }

    //endregion
}
