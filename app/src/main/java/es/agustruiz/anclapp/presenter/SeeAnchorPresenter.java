package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.util.Log;

import es.agustruiz.anclapp.ui.anchor.seeAnchor.SeeAnchorActivity;

public class SeeAnchorPresenter implements Presenter {

    public static final String LOG_TAG = SeeAnchorPresenter.class.getName() + "[A]";

    SeeAnchorActivity mActivity;
    Context mContext;

    //region [Public methods]

    public SeeAnchorPresenter(SeeAnchorActivity activity) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public void editAnchor() {
        Log.d(LOG_TAG, "editAnchor");
    }

    public void removeAnchor() {
        Log.d(LOG_TAG, "removeAnchor");
    }

    //endregion

    //region [Presenter overriden methods]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(message);
    }

    //endregion
}
