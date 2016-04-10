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

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        mContext = getContext();
        mGoogleMapFragmentPresenter = new GoogleMapFragmentPresenter(this);
        return v;
    }

    //region [Overriden methods]

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
}
