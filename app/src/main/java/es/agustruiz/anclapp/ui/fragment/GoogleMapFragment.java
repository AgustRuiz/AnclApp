package es.agustruiz.anclapp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.presenter.GoogleMapFragmentPresenter;

public class GoogleMapFragment extends Fragment {

    public static final String LOG_TAG = GoogleMapFragment.class.getName() + "[A]";

    protected GoogleMapFragmentPresenter mGoogleMapFragmentPresenter;
    protected Context mContext;

    public final char CENTER_MAP_OFF = 0;
    public final char CENTER_MAP_CURRENT_LOCATION = 1;
    public final char CENTER_MAP_MARKER = 2;
    protected char autoCenterMapMode = CENTER_MAP_OFF;
    protected final String AUTO_CENTER_MAP_MODE_TAG = "autoCenterMapMode";

    //region [Overriden methods]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        mContext = getContext();
        mGoogleMapFragmentPresenter = new GoogleMapFragmentPresenter(this);

        if (savedInstanceState != null) {
            autoCenterMapMode = savedInstanceState.getChar(AUTO_CENTER_MAP_MODE_TAG);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putChar(AUTO_CENTER_MAP_MODE_TAG, autoCenterMapMode);
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

    //endregion
}
