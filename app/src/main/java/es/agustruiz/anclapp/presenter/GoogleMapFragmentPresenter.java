package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.anchor.SeeAnchorActivity;
import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;

public class GoogleMapFragmentPresenter {

    public static final String LOG_TAG = GoogleMapFragmentPresenter.class.getName() + "[A]";

    protected static final long LOCATION_REQUEST_INTERVAL = 1000 * 10; // 10 milliseconds
    protected static final long LOCATION_REQUEST_FATEST_INTERVAL = 1000 * 5; // 5 milliseconds

    protected GoogleMapFragment mFragment;
    protected Context mContext = null;
    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mCurrentLocation = null;
    protected LocationRequest mLocationRequest = null;
    protected EventsUtil mEventsUtil = EventsUtil.getInstance();
    protected AnchorDAO mAnchorDAO = null;

    //region [Public methods]

    public GoogleMapFragmentPresenter(GoogleMapFragment fragment) {
        mFragment = fragment;
        mContext = mFragment.getContext();

        createLocationRequest();

        mGoogleApiClient = new GoogleApiClient
                .Builder(mContext)
                .addApi(LocationServices.API) //.addApi(AppIndex.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        //Log.i(LOG_TAG, "GoogleApiClient.ConnectionCallbacks.onConnected");
                        startLocationUpdates();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        //Log.i(LOG_TAG, "GoogleApiClient.ConnectionCallbacks.onConnectionSuspender");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {
                        //Log.i(LOG_TAG, "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed");
                    }
                })
                .build();

        mEventsUtil.addEventListener(EventsUtil.CENTER_MAP_ON_CURRENT_LOCATION, new IEventHandler() {
            @Override
            public void callback(Event event) {
                if (event.getParameter().equals(true)) {
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_CURRENT_LOCATION);
                }else{
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_OFF);
                }
            }
        });

        mEventsUtil.addEventListener(EventsUtil.MAP_CANCEL_NEW_MARKER, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mFragment.removeNewMarker();
                mEventsUtil.setMarkerDetails(mCurrentLocation);
            }
        });

        mEventsUtil.addEventListener(EventsUtil.REFRESH_ANCHOR_MARKERS, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mFragment.reloadAnchorsInMap();
            }
        });
    }

    public void GoogleApiClientConnect() {
        mGoogleApiClient.connect();
    }

    public void GoogleApiClientDisconnect() {
        mGoogleApiClient.disconnect();
    }

    public List<Anchor> getAnchorList() {
        prepareAnchorDAO();
        mAnchorDAO.openReadOnly();
        List<Anchor> result = mAnchorDAO.getAll();
        mAnchorDAO.close();
        return result;
    }

    public void seeAnchor(long id){
        Intent intent = new Intent(mContext, SeeAnchorActivity.class);
        intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, id);
        mContext.startActivity(intent);
    }

    public Location getCurrentLocation(){
        return mCurrentLocation;
    }

    //endregion

    //region [Private methods]

    private void createLocationRequest() {
        //Log.d(LOG_TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        //Log.d(LOG_TAG, "Location update started");
        //if (mGoogleApiClient.isConnected()) {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        //mEventsUtil.currentLocationChange(mCurrentLocation);
                        if (mFragment.isAutoCenterMapCurrentLocation()){
                            mFragment.centerMapOnLocation(mCurrentLocation);
                            mEventsUtil.setMarkerDetails(mCurrentLocation);
                        }else if(!mFragment.isAutoCenterMapModeOnMarker()){
                            mEventsUtil.setMarkerDetails(location);
                        }
                    }
                }); // TODO Permission check
        //}
    }

    private void prepareAnchorDAO(){
        if(mAnchorDAO==null){
            mAnchorDAO = new AnchorDAO(mContext);
        }
    }

    //endregion

}
