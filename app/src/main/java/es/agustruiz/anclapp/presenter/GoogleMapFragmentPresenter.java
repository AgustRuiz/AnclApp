package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;

public class GoogleMapFragmentPresenter implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String LOG_TAG = GoogleMapFragmentPresenter.class.getName() + "[A]";

    GoogleMapFragment mFragment;

    protected Context mContext = null;
    protected GoogleApiClient mGoogleApiClient = null;
    protected GoogleMap mGoogleMap = null;
    protected LocationRequest mLocationRequest = null;

    protected static final long INTERVAL = 1000 * 10; // 10 milliseconds
    protected static final long FATEST_INTERVAL = 1000 * 5; // 5 milliseconds

    public GoogleMapFragmentPresenter(GoogleMapFragment fragment){
        mFragment = fragment;
        mContext = mFragment.getContext();

        mGoogleApiClient = new GoogleApiClient
                .Builder(mContext)
                .addApi(LocationServices.API) //.addApi(AppIndex.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        createLocationRequest();
    }

    //region [Private methods]

    private void createLocationRequest() {
        Log.i(LOG_TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void startLocationUpdates() {
        Log.e(LOG_TAG, "Location update started");
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this); // TODO Permission check
    }

    //endregion

    //region [OnMapReadyCallback]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
    }

    //endregion

    //region [GoogleApiClient.ConnectionCallbacks]

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(LOG_TAG, "GoogleApiClient.ConnectionCallbacks.onConnected");
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "GoogleApiClient.ConnectionCallbacks.onConnectionSuspender");
    }

    //endregion

    //region [GoogleApiClient.OnConnectionFailedListener]

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(LOG_TAG, "GoogleApiClient.OnConnectionFailedListener.onConnectionFailed");
    }

    //endregion

    //region [LocationListener]

    @Override
    public void onLocationChanged(Location location) {
        Log.i(LOG_TAG, "LocationListener.onLocationChanged");
        /*mCurrentLocation = location;
        if (mIsViewCentered) {
            centerMapInCurrentLocation();
        }/**/
    }

    //endregion
}
