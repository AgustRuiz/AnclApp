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
                LatLng latLng = (LatLng) event.getParameter();
                showMessage("Long press at " + latLng.latitude + "," + latLng.longitude);
            }
        });

    }

    public void centerMapOnCurrentLocation() {
        EventsUtil.getInstance().centerMapOnLocationEvent();
    }

    public void addAnchor(){
        showMessage("Add anchor here");
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
