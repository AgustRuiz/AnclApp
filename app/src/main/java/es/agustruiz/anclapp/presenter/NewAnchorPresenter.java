package es.agustruiz.anclapp.presenter;

import android.content.Context;
import android.widget.Toast;

import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.newAnchor.NewAnchorActivity;

public class NewAnchorPresenter implements Presenter {

    public static final String LOG_TAG = NewAnchorPresenter.class.getName() + "[A]";

    NewAnchorActivity mActivity;
    Context mContext;

    //region [Public methods]

    public NewAnchorPresenter(NewAnchorActivity activity) {
        mActivity = activity;
        mContext = activity.getApplicationContext();
    }

    public void saveNewAnchor() {
        boolean resultOk = false;
        if(isNewAnchorDataOk()) {
            AnchorDAO mAnchorDAO = new AnchorDAO(mContext);
            try {
                Anchor newAnchor = new Anchor(
                        null,
                        mActivity.getLatitudeValue(),
                        mActivity.getLongitudeValue(),
                        mActivity.getTitleValue(),
                        mActivity.getDescriptionValue(),
                        mActivity.getColorValue(),
                        mActivity.getReminderValue()
                );
                mAnchorDAO.openWritable();
                mAnchorDAO.add(newAnchor);
                resultOk = true;
            } catch (Exception ignored) {
            } finally {
                mAnchorDAO.close();
            }
        }else{
            showMessage("Check anchor data");
        }

        if(resultOk){
            // TODO send message with snackbar?
            Toast.makeText(mContext, "Anchor created!", Toast.LENGTH_SHORT).show();
            mActivity.finish();
        }
    }

    //endregion

    //region [Presenter overeriden methods]

    @Override
    public void showMessage(String message) {
        mActivity.showMessageView(message);
    }

    //endregion


    //region [Private methods]

    private boolean isNewAnchorDataOk() {
        boolean result = true;
        if (mActivity.getTitleValue().length() == 0) {
            result = false;
        }
        return result;
    }

    //endregion

}
