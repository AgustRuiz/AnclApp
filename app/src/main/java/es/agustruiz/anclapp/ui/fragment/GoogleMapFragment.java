package es.agustruiz.anclapp.ui.fragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.presenter.GoogleMapFragmentPresenter;
import es.agustruiz.anclapp.ui.AlertDialogFragment;

public class GoogleMapFragment extends Fragment {

    public static final String LOG_TAG = GoogleMapFragment.class.getName() + "[A]";

    //region [Varibles]

    protected GoogleMapFragmentPresenter mPresenter;
    protected Context mContext;
    protected EventsUtil mEventsUtil = EventsUtil.getInstance();

    protected SupportMapFragment mMapFragment;
    protected GoogleMap mGoogleMap;

    protected Marker mNewMarker = null;
    protected Map<Marker, Long> mMarkerMap = new ArrayMap<>();
    protected MarkerOptions mNewMarkerOptions = null;
    protected String mNewMarkerColor = null;

    public final char CENTER_MAP_OFF = 0;
    public final char CENTER_MAP_CURRENT_LOCATION = 1;
    public final char CENTER_MAP_MARKER = 2;
    protected char autoCenterMapMode = CENTER_MAP_OFF;
    protected final String AUTO_CENTER_MAP_MODE_TAG = "autoCenterMapMode";
    protected final String NEW_MARKER_OPTIONS_TAG = "mNewMarkerOptions";

    protected static final float MAP_MIN_ZOOM = 15;

    //endregion

    //region [Fragment methods]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = getContext();
        mPresenter = new GoogleMapFragmentPresenter(this);
        initializeSavedInstanceState(savedInstanceState);
        initializeMapFragment();
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
    public void onResume() {
        super.onResume();
        reloadAnchorsInMap();
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.GoogleApiClientConnect();
    }

    @Override
    public void onStop() {
        super.onResume();
        mPresenter.GoogleApiClientDisconnect();
    }

    //endregion

    //region [Public methods]

    public boolean isAutoCenterMapCurrentLocation() {
        return autoCenterMapMode == CENTER_MAP_CURRENT_LOCATION;
    }

    public void setAutoCenterMapMode(char mode) {
        autoCenterMapMode = mode;
        switch (autoCenterMapMode){
            case CENTER_MAP_CURRENT_LOCATION:
                Log.d(LOG_TAG, "center map in current position activated");
                centerMapOnLocation(mPresenter.getCurrentLocation());
                break;
            case CENTER_MAP_MARKER:
            case CENTER_MAP_OFF:
                break;
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

    public void reloadAnchorsInMap() {
        Log.d(LOG_TAG, "reloadAnchorsInMap");
        if (mGoogleMap != null) {
            removeAnchorsInMap();
            for (Anchor anchor : mPresenter.getAnchorList()) {
                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(anchor.getLatLng())
                        .title(anchor.getTitle())
                        .icon(getMarkerIcon(anchor.getColor()))
                        .draggable(false));

                if (anchor.getDescription().length() > 0) {
                    marker.setSnippet(anchor.getDescription());
                }
                mMarkerMap.put(marker, anchor.getId());
            }
        }
    }

    public Marker getNewMarkerView(){
        return mNewMarker;
    }

    public void removeNewMarkerView(){
        if (mNewMarker != null) {
            mNewMarker.remove();
            mNewMarker = null;
            mNewMarkerOptions = null;
        }
    }

    public void showAlertNoGps(){
        DialogFragment alertDialogFragment = AlertDialogFragment.newInstance(
                getString(R.string.no_location_provider_found),
                getString(R.string.no_location_provider_message));
        alertDialogFragment.setCancelable(false);
        alertDialogFragment.show(getFragmentManager(), "alert");
    }

    //endregion

    //region [Private methods]

    private void initializeMapFragment() {
        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mGoogleMap = googleMap;
                mGoogleMap.setMyLocationEnabled(true); // TODO Permission check
                mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        removeNewMarkerView();
                        createNewMarkerView(latLng);
                        centerMapOnLocation(latLng);
                        mPresenter.notifyNewMarkerData(latLng);
                        setAutoCenterMapMode(CENTER_MAP_MARKER);
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
                        centerMapOnLocation(marker.getPosition());
                        mPresenter.notifyNewMarkerData(marker.getPosition());
                    }
                });
                mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        if (mMarkerMap.get(marker) != null) {
                            setAutoCenterMapMode(CENTER_MAP_OFF);
                            mEventsUtil.dismissFabCenterMap();


                        }
                        return false;
                    }
                });
                mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Long anchorId = mMarkerMap.get(marker);
                        if (anchorId != null) {
                            mPresenter.launchSeeAnchorActivity(anchorId);
                        }
                    }
                });
                if (mNewMarkerOptions != null) {
                    mNewMarker = mGoogleMap.addMarker(mNewMarkerOptions);
                }
                reloadAnchorsInMap();
            }
        });
    }

    private void initializeSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            autoCenterMapMode = savedInstanceState.getChar(AUTO_CENTER_MAP_MODE_TAG);
            mNewMarkerOptions = savedInstanceState.getParcelable(NEW_MARKER_OPTIONS_TAG);
        }
    }

    private void createNewMarkerView(LatLng latLng){
        mNewMarkerColor = PreferenceManager.getDefaultSharedPreferences(mContext)
                .getString(
                        getString(R.string.key_pref_anchors_color),
                        getString(R.string.pref_anchors_color_default_value)
                );
        mNewMarkerOptions = new MarkerOptions()
                .position(latLng)
                .icon(getMarkerIcon(mNewMarkerColor))
                .draggable(true);
        mNewMarker = mGoogleMap.addMarker(mNewMarkerOptions);
    }

    private void removeAnchorsInMap() {
        for (Map.Entry<Marker, Long> item : mMarkerMap.entrySet()) {
            item.getKey().remove();
        }
        mMarkerMap.clear();
    }

    private BitmapDescriptor getMarkerIcon(String color) {
        Bitmap ob = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_place_white_36dp);
        Bitmap obm = Bitmap.createBitmap(ob.getWidth(), ob.getHeight(), ob.getConfig());
        Canvas canvas = new Canvas(obm);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(Color.parseColor(color), PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(ob, 0f, 0f, paint);
        return BitmapDescriptorFactory.fromBitmap(obm);
    }

    //endregion
}
