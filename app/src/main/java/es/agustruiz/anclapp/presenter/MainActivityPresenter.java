package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.MainActivity;
import es.agustruiz.anclapp.ui.anchor.NewAnchorActivity;

public class MainActivityPresenter implements Presenter {

    public static final String LOG_TAG = MainActivityPresenter.class.getName()+"[A]";

    MainActivity mActivity;
    Context mContext;
    Location mCurrentLocation = null;
    EventsUtil eventsUtil = EventsUtil.getInstance();

    Double mIntentLatitude = null;
    Double mIntentLongitude = null;
    String mIntentDescription = null;

    //region [Public methods]

    public MainActivityPresenter(MainActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();

        eventsUtil.addEventListener(EventsUtil.MAP_NEW_MARKER, new IEventHandler() {
            @Override
            public void callback(Event event) {
                LatLng latLng = (LatLng) event.getParameter();
                fillLocationCardView(latLng);
                mActivity.hideFabCenterView();
                mActivity.showFabDismissCardView();
                mActivity.showLocationCardView();

                String description = "";
                Geocoder geocoder = new Geocoder(mContext);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    if(addresses.size()>0){
                        description = addresses.get(0).getAddressLine(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fillIntentExtras(latLng.latitude, latLng.longitude, description);
            }
        });
        eventsUtil.addEventListener(EventsUtil.CURRENT_LOCATION_CHANGE, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mCurrentLocation = (Location) event.getParameter();
            }
        });
        eventsUtil.addEventListener(EventsUtil.GET_MARKER_DETAILS, new IEventHandler(){
            @Override
            public void callback(Event event) {
                Location location = (Location) event.getParameter();
                String description = "";
                Geocoder geocoder = new Geocoder(mContext);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if(addresses.size()>0){
                        description = addresses.get(0).getAddressLine(0);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                fillIntentExtras(location.getLatitude(), location.getLongitude(), description);
            }
        });
    }

    public void centerMapOnCurrentLocation(boolean state) {
        eventsUtil.centerMapOnCurrentLocationEvent(state);
    }

    public void addAnchor() {
        if(mIntentLatitude != null && mIntentLongitude != null) {
            Intent intent = new Intent(mContext, NewAnchorActivity.class);
            intent.putExtra(NewAnchorActivity.LATITUDE_INTENT_TAG, mIntentLatitude);
            intent.putExtra(NewAnchorActivity.LONGITUDE_INTENT_TAG, mIntentLongitude);
            intent.putExtra(NewAnchorActivity.DESCRIPTION_INTENT_TAG, mIntentDescription);
            mActivity.startActivity(intent);
        }else{
            showMessage(mActivity.getString(R.string.no_location_found));
        }
    }

    public void cancelMarker() {
        mActivity.hideLocationCardView();
        mActivity.setFabCenterViewState(false);
        mActivity.showFabCenterView();
        mActivity.hideFabDismissCardView();
        eventsUtil.cancelNewMarker();
    }

    private void fillLocationCardView(LatLng latLng) {
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
                        ? (Math.round(mCurrentLocation.distanceTo(markerLocation) / 10) / 100f) + mContext.getString(R.string.km_unit)
                        : ""; // TODO check this
                mActivity.fillLocationCardView(
                        address.getAddressLine(0),
                        address.getLocality(),
                        distance);
            }
        } catch (IOException e) {
            // TODO Check this
            mActivity.fillLocationCardView("Error", e.getMessage(), "?");
        }
    }

    public void refreshAnchorMarkers(){
        eventsUtil.refreshAnchorMarkers();
    }

    //endregion

    //region [Presenter]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(null, message);
    }

    //endregion

    //region [Private methods]

    private void fillIntentExtras(Double latitude, Double longitude, String description){
        mIntentLatitude = latitude;
        mIntentLongitude = longitude;
        mIntentDescription = description;
    }

    //endregion
}
