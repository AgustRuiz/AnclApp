package es.agustruiz.anclapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.SupportMapFragment;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.presenter.GoogleMapFragmentPresenter;

public class GoogleMapFragment extends Fragment {

    public static final String LOG_TAG = GoogleMapFragment.class.getName() + "[A]";

    protected GoogleMapFragmentPresenter mGoogleMapFragmentPresenter;

    protected SupportMapFragment mMapFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);

        mGoogleMapFragmentPresenter = new GoogleMapFragmentPresenter(this);

        mMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(mGoogleMapFragmentPresenter);

        return v;
    }
}
