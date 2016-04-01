package es.agustruiz.anclapp.ui.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import es.agustruiz.anclapp.R;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String LOG_TAG = MapFragment.class.getName() + "[A]";

    protected GoogleMap mGoogleMap = null;
    protected GoogleApiClient mGoogleApiClient = null;
    protected LocationRequest mLocationRequest = null;

    protected static final long INTERVAL = 1000 * 10; // 10 milliseconds
    protected static final long FATEST_INTERVAL = 1000 * 5; // 5 milliseconds

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        Toast.makeText(getContext(), "onCreateView", Toast.LENGTH_LONG).show();


        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createLocationRequest();
        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(LocationServices.API) //.addApi(AppIndex.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();




        return v;
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
        //if (mGoogleApiClient.isConnected()) {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this); // TODO Permission check
        //}
    }/**/

    //endregion

    //region [OnMapReadyCallback]

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        Toast.makeText(getContext(), "Map Ready", Toast.LENGTH_LONG).show();
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
