package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;

public class GoogleMapFragmentPresenter {

    public static final String LOG_TAG = GoogleMapFragmentPresenter.class.getName() + "[A]";

    protected static final long LOCATION_REQUEST_INTERVAL = 1000 * 10; // 10 milliseconds
    protected static final long LOCATION_REQUEST_FATEST_INTERVAL = 1000 * 5; // 5 milliseconds

    protected static final float MAP_MIN_ZOOM = 15;

    protected GoogleMapFragment mFragment;
    protected SupportMapFragment mMapFragment;
    protected Context mContext = null;
    protected GoogleMap mGoogleMap;
    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mCurrentLocation = null;
    protected LocationRequest mLocationRequest = null;
    protected Marker mMarker = null;
    protected EventsUtil mEventsUtil = EventsUtil.getInstance();

    //region [Public methods]

    public GoogleMapFragmentPresenter(GoogleMapFragment fragment) {
        mFragment = fragment;
        mContext = mFragment.getContext();

        mMapFragment = (SupportMapFragment) mFragment.getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                Log.i(LOG_TAG, "Map ready");
                mGoogleMap = googleMap;
                mGoogleMap.setMyLocationEnabled(true); // TODO Permission check
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        /*Toast.makeText(mContext, "Long press at " + latLng.latitude + ","
                                + latLng.longitude, Toast.LENGTH_SHORT).show();/**/
                        if(mMarker!=null){
                            removeMarker();
                        }
                        mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_MARKER);
                        centerMapOnLocation(latLng);
                        mEventsUtil.mapClick(latLng);
                        mMarker = mGoogleMap.addMarker(new MarkerOptions()
                                .position(latLng));
                    }
                });
            }
        });
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

        mEventsUtil.addEventListener(EventsUtil.FAB_CENTER_MAP, new IEventHandler() {
            @Override
            public void callback(Event event) {
                if (event.getParameter().equals(true)) {
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_CURRENT_LOCATION);
                    centerMapOnLocation(mCurrentLocation);
                } else {
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_OFF);
                }
            }
        });

        mEventsUtil.addEventListener(EventsUtil.CANCEL_NEW_MARKER, new IEventHandler() {
            @Override
            public void callback(Event event) {
                removeMarker();
            }
        });
    }

    public void GoogleApiClientConnect() {
        mGoogleApiClient.connect();
    }

    public void GoogleApiClientDisconnect() {
        mGoogleApiClient.disconnect();
    }

    public void centerMapOnLocation(Location location) {
        if (location != null) {
            centerMapOnLocation(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    public void removeMarker(){
        if(mMarker!=null){
            mMarker.remove();
            mMarker = null;
        }
    }

    public void centerMapOnLocation(LatLng latLng) {
        if (latLng != null) {
            float currentZoom = mGoogleMap.getCameraPosition().zoom;
            CameraUpdate camera = CameraUpdateFactory
                    .newLatLngZoom(latLng,
                            (currentZoom < MAP_MIN_ZOOM ? MAP_MIN_ZOOM : currentZoom));
            mGoogleMap.animateCamera(camera);
        }
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
                        //Log.d(LOG_TAG, "Location changed (accuracy: " + location.getAccuracy() + ")");
                        mCurrentLocation = location;
                        mEventsUtil.currentLocationChange(mCurrentLocation);
                        if (mFragment.isAutoCenterMapCurrentOnLocation())
                            centerMapOnLocation(location);
                    }
                }); // TODO Permission check
        //}
    }

    //endregion

}
