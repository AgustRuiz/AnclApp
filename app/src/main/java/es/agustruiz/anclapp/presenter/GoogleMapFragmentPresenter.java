package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

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
    protected EventsUtil eventsUtil = EventsUtil.getInstance();

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
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);

                mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        Toast.makeText(mContext, "Long press", Toast.LENGTH_SHORT).show();
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

        eventsUtil.addEventListener(EventsUtil.FAB_CENTER_MAP, new IEventHandler() {
            @Override
            public void callback(Event event) {
                //Log.d(LOG_TAG, "fabCenterMap listener callback zoom!");
                mFragment.switchAutoCenterMapOnLocation();
                centerMapOnLocation(mCurrentLocation);
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
            float currentZoom = mGoogleMap.getCameraPosition().zoom;
            CameraUpdate camera = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),
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
                        if(mFragment.isAutoCenterMapOnLocation())
                            centerMapOnLocation(location);
                    }
                }); // TODO Permission check
        //}
    }

    //endregion

}