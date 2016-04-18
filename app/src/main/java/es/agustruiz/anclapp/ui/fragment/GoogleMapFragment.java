package es.agustruiz.anclapp.ui.fragment;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.presenter.GoogleMapFragmentPresenter;

public class GoogleMapFragment extends Fragment {

    public static final String LOG_TAG = GoogleMapFragment.class.getName() + "[A]";

    protected GoogleMapFragmentPresenter mGoogleMapFragmentPresenter;
    protected Context mContext;
    protected EventsUtil mEventsUtil = EventsUtil.getInstance();

    protected SupportMapFragment mMapFragment;
    protected GoogleMap mGoogleMap;
    protected Marker mNewMarker = null;
    protected MarkerOptions mNewMarkerOptions = null;
    protected final String NEW_MARKER_OPTIONS_TAG = "mNewMarkerOptions";


    public final char CENTER_MAP_OFF = 0;
    public final char CENTER_MAP_CURRENT_LOCATION = 1;
    public final char CENTER_MAP_MARKER = 2;
    protected char autoCenterMapMode = CENTER_MAP_OFF;
    protected final String AUTO_CENTER_MAP_MODE_TAG = "autoCenterMapMode";


    protected static final float MAP_MIN_ZOOM = 15;

    //region [Overriden methods]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        mContext = getContext();
        mGoogleMapFragmentPresenter = new GoogleMapFragmentPresenter(this);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
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
                        if (mNewMarker != null) {
                            removeMarker();
                        }
                        setAutoCenterMapMode(CENTER_MAP_MARKER);
                        centerMapOnLocation(latLng);
                        mNewMarkerOptions = new MarkerOptions()
                                .position(latLng)
                                .draggable(true);
                        mNewMarker = mGoogleMap.addMarker(mNewMarkerOptions);
                        mEventsUtil.mapClick(latLng);
                    }
                });
                mGoogleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                    @Override
                    public void onMarkerDragStart(Marker marker) {
                    }

                    @Override
                    public void onMarkerDrag(Marker marker) {
                    }

                    @Override
                    public void onMarkerDragEnd(Marker marker) {
                        mEventsUtil.mapClick(marker.getPosition());
                    }
                });

                if(mNewMarkerOptions !=null){
                    mNewMarker = mGoogleMap.addMarker(mNewMarkerOptions);
                }
            }
        });

        if (savedInstanceState != null) {
            autoCenterMapMode = savedInstanceState.getChar(AUTO_CENTER_MAP_MODE_TAG);
            mNewMarkerOptions = savedInstanceState.getParcelable(NEW_MARKER_OPTIONS_TAG);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putChar(AUTO_CENTER_MAP_MODE_TAG, autoCenterMapMode);
        if (mNewMarkerOptions != null)
            outState.putParcelable(NEW_MARKER_OPTIONS_TAG, mNewMarkerOptions);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleMapFragmentPresenter.GoogleApiClientConnect();
    }

    @Override
    public void onStop() {
        super.onResume();
        mGoogleMapFragmentPresenter.GoogleApiClientDisconnect();
    }

    //endregion

    //region [Public methods]

    public boolean isAutoCenterMapCurrentOnLocation() {
        return autoCenterMapMode == CENTER_MAP_CURRENT_LOCATION;
    }

    public boolean isAutoCenterMapModeOnMarker() {
        return autoCenterMapMode == CENTER_MAP_MARKER;
    }

    public boolean isAutoCenterMapModeOff() {
        return autoCenterMapMode == CENTER_MAP_OFF;
    }

    public boolean setAutoCenterMapMode(char mode) {
        if (mode != CENTER_MAP_CURRENT_LOCATION && mode != CENTER_MAP_MARKER && mode != CENTER_MAP_OFF)
            return false;
        else {
            autoCenterMapMode = mode;
            return true;
        }
    }

    public char switchAutoCenterMapOnLocation() {
        if (autoCenterMapMode != CENTER_MAP_MARKER) {
            autoCenterMapMode = (autoCenterMapMode == CENTER_MAP_CURRENT_LOCATION ? CENTER_MAP_OFF : CENTER_MAP_CURRENT_LOCATION);
        }
        return autoCenterMapMode;
    }

    public void removeMarker() {
        if (mNewMarker != null) {
            mNewMarker.remove();
            mNewMarker = null;
            mNewMarkerOptions = null;
        }
    }

    public void centerMapOnLocation(Location location) {
        if (location != null) {
            centerMapOnLocation(new LatLng(location.getLatitude(), location.getLongitude()));
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
}
