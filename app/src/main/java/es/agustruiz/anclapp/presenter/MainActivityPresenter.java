package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.view.View;

import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.ui.MainActivity;

public class MainActivityPresenter implements Presenter{

    MainActivity mActivity;
    Context mContext;

    //region [Public methods]

    public MainActivityPresenter(MainActivity activity){
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public void centerMapOnCurrentLocation() {
        EventsUtil.getInstance().centerMapOnLocationEvent();
    }

    public void addAnchor(){
        showMessage(null, "Add anchor here");
    }

    //endregion

    //region [Presenter]

    @Override
    public void showMessage(View view, String message) {
        mActivity.showMessageView(view, message);
    }

    //endregion
}
