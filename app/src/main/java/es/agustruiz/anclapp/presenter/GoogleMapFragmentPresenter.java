package es.agustruiz.anclapp.presenter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.anchor.NewAnchorActivity;
import es.agustruiz.anclapp.ui.anchor.SeeAnchorActivity;
import es.agustruiz.anclapp.ui.fragment.GoogleMapFragment;

public class GoogleMapFragmentPresenter{

    public static final String LOG_TAG = GoogleMapFragmentPresenter.class.getName() + "[A]";

    //region [Variables]

    protected static final long LOCATION_REQUEST_INTERVAL = 1000 * 10; // 10 milliseconds
    protected static final long LOCATION_REQUEST_FATEST_INTERVAL = 1000 * 5; // 5 milliseconds

    protected GoogleMapFragment mFragment;
    protected Context mContext = null;
    protected GoogleApiClient mGoogleApiClient = null;
    protected Location mCurrentLocation = null;
    protected LocationRequest mLocationRequest = null;
    protected EventsUtil mEventsUtil = EventsUtil.getInstance();
    protected AnchorDAO mAnchorDAO = null;

    protected String mNewMarkerAddress = "";
    protected String mNewMarkerLocality = "";
    protected String nNewMarkerDistance = "";

    //endregion

    //region [Public methods]

    public GoogleMapFragmentPresenter(GoogleMapFragment fragment) {
        mFragment = fragment;
        mContext = mFragment.getContext();
        createLocationRequest();
        initializeGoogleApiClient();
        registerEvents();
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

    public void launchSeeAnchorActivity(long id) {
        Intent intent = new Intent(mContext, SeeAnchorActivity.class);
        intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, id);
        mContext.startActivity(intent);
    }

    public Location getCurrentLocation() {
        return mCurrentLocation;
    }

    public void notifyNewMarkerData(LatLng latLng) {
        fillNewMarkerDescription(latLng);
        String[] data = new String[]{mNewMarkerAddress, mNewMarkerLocality, nNewMarkerDistance};
        mEventsUtil.getNewMarkerData(data);
    }

    //endregion

    //region [Private methods]

    private void initializeGoogleApiClient() {
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
    }

    private void registerEvents() {
        mEventsUtil.addEventListener(EventsUtil.CENTER_MAP_ON_CURRENT_LOCATION, new IEventHandler() {
            @Override
            public void callback(Event event) {
                if (event.getParameter().equals(true))
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_CURRENT_LOCATION);
                else
                    mFragment.setAutoCenterMapMode(mFragment.CENTER_MAP_OFF);
            }
        });

        mEventsUtil.addEventListener(EventsUtil.ADD_NEW_ANCHOR_ON_MAP, new IEventHandler() {
            @Override
            public void callback(Event event) {
                Marker marker = mFragment.getNewMarkerView();
                if (marker != null) {
                    Intent intent = new Intent(mContext, NewAnchorActivity.class);
                    intent.putExtra(NewAnchorActivity.LATITUDE_INTENT_TAG, marker.getPosition().latitude);
                    intent.putExtra(NewAnchorActivity.LONGITUDE_INTENT_TAG, marker.getPosition().longitude);
                    intent.putExtra(NewAnchorActivity.DESCRIPTION_INTENT_TAG, mNewMarkerAddress);
                    mEventsUtil.hideLocationCard();
                    mFragment.removeNewMarkerView();
                    mContext.startActivity(intent);
                } else if (mCurrentLocation != null) {
                    fillNewMarkerDescription(mCurrentLocation);
                    Intent intent = new Intent(mContext, NewAnchorActivity.class);
                    intent.putExtra(NewAnchorActivity.LATITUDE_INTENT_TAG, mCurrentLocation.getLatitude());
                    intent.putExtra(NewAnchorActivity.LONGITUDE_INTENT_TAG, mCurrentLocation.getLongitude());
                    intent.putExtra(NewAnchorActivity.DESCRIPTION_INTENT_TAG, mNewMarkerAddress);
                    mContext.startActivity(intent);
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.no_location_found),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


        mEventsUtil.addEventListener(EventsUtil.MAP_CANCEL_NEW_MARKER, new IEventHandler() {
            @Override
            public void callback(Event event) {
                removeNewMarker();
            }
        });

        mEventsUtil.addEventListener(EventsUtil.REFRESH_ANCHOR_MARKERS, new IEventHandler() {
            @Override
            public void callback(Event event) {
                mFragment.reloadAnchorsInMap();
            }
        });
    }

    private void createLocationRequest() {
        //Log.d(LOG_TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAL);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        mCurrentLocation = location;
                        if (mFragment.isAutoCenterMapCurrentLocation()) {
                            mFragment.centerMapOnLocation(mCurrentLocation);
                        }
                        Anchor.setReferenceLocation(mCurrentLocation);
                        mEventsUtil.notifyCurrentLocationChanged(mCurrentLocation);
                    }
                });

        LocationManager mLocationService = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        mLocationService.addGpsStatusListener(new GpsStatus.Listener() {
            @Override
            public void onGpsStatusChanged(int event) {
                switch (event) {
                    case GpsStatus.GPS_EVENT_STARTED:
                        // Do Something with mStatus info
                        Log.d(LOG_TAG, "GPS_EVENT_STARTED");
                        break;

                    case GpsStatus.GPS_EVENT_STOPPED:
                        // Do Something with mStatus info
                        Log.d(LOG_TAG, "GPS_EVENT_STOPPED");
                        mFragment.showAlertNoGps();
                        break;
                }
            }
        });
        if(!mLocationService.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            mFragment.showAlertNoGps();
        }
    }

    private void fillNewMarkerDescription(Location location) {
        fillNewMarkerDescription(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    private void fillNewMarkerDescription(LatLng latLng) {
        boolean isError = false;
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            Location markerLocation = new Location(LocationManager.GPS_PROVIDER);
            markerLocation.setLatitude(latLng.latitude);
            markerLocation.setLongitude(latLng.longitude);
            if(Geocoder.isPresent()) {
                List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                if (addresses.size() == 0) {
                    isError = true;
                } else {
                    Address address = addresses.get(0);
                    mNewMarkerAddress = address.getAddressLine(0);
                    mNewMarkerLocality = address.getLocality();
                    nNewMarkerDistance = (mCurrentLocation != null)
                            ? (Math.round(mCurrentLocation.distanceTo(markerLocation) / 10) / 100f)
                            + mContext.getString(R.string.km_unit)
                            : "";
                }
            }else{
                mNewMarkerAddress = mContext.getString(R.string.cant_load_geocoder);
                mNewMarkerLocality = "";
                nNewMarkerDistance = (mCurrentLocation != null)
                        ? (Math.round(mCurrentLocation.distanceTo(markerLocation) / 10) / 100f)
                        + mContext.getString(R.string.km_unit)
                        : "";
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Geocoder error: " + e.toString(), e);
            isError = true;
        }
        if (isError) {
            mNewMarkerAddress = mContext.getString(R.string.no_location_found);
            mNewMarkerLocality = nNewMarkerDistance = "";
        }
    }

    private void removeNewMarker() {
        mFragment.removeNewMarkerView();
        fillNewMarkerDescription(getCurrentLatLng());
    }

    private void prepareAnchorDAO() {
        if (mAnchorDAO == null) {
            mAnchorDAO = new AnchorDAO(mContext);
        }
    }

    private LatLng getCurrentLatLng() {
        return new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
    }

    //endregion

}
