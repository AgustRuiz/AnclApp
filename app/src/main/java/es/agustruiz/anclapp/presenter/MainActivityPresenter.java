package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.view.View;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.MainActivity;

public class MainActivityPresenter implements Presenter{

    MainActivity mActivity;
    Context mContext;
    Location mCurrentLocation = null;
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
                    fillLocationCardView(latLng);
                    mActivity.hideFabCenterView();
                    mActivity.showLocationCardView();
                }
            }
        });
        eventsUtil.addEventListener(EventsUtil.CURRENT_LOCATION_CHANGE, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mCurrentLocation = (Location) event.getParameter();
            }
        });
    }

    public void centerMapOnCurrentLocation() {
        eventsUtil.centerMapOnLocationEvent(mActivity.isAutoCenterMap());
    }

    public void addAnchor(){
        showMessage("Add anchor here");
    }

    public void cancelMarker() {
        mActivity.hideLocationCardView();
        mActivity.setAutoCenterMap(false);
        mActivity.showFabCenterView();
        eventsUtil.cancelNewMarker();
    }

    public void fillLocationCardView(LatLng latLng){
        Geocoder geocoder = new Geocoder(mContext);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() == 0) {
                // TODO Check this
                mActivity.fillLocationCardView("Null", "Null", "Null");
            } else {
                Address address = addresses.get(0);
                Location markerLocation = new Location(LocationManager.GPS_PROVIDER);
                markerLocation.setLatitude(latLng.latitude);
                markerLocation.setLongitude(latLng.longitude);
                String distance = (mCurrentLocation != null)
                                ? (Math.round(mCurrentLocation.distanceTo(markerLocation)/10)/100f) + mContext.getString(R.string.km_unit)
                                : ""; // TODO check this
                mActivity.fillLocationCardView(
                        address.getAddressLine(0),
                        address.getAddressLine(0),
                        distance);
            }
        } catch (IOException e) {
            // TODO Check this
            mActivity.fillLocationCardView("Error", e.getMessage(), "?");
        }
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
