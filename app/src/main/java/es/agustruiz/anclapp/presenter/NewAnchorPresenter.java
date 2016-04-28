package es.agustruiz.anclapp.presenter;

import android.content.Context;

import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.newAnchor.NewAnchorActivity;

public class NewAnchorPresenter {

    public static final String LOG_TAG = NewAnchorPresenter.class.getName() + "[A]";

    NewAnchorActivity mActivity;
    Context mContext;

    //region [Public methods]

    public NewAnchorPresenter(NewAnchorActivity activity){
        mActivity = activity;
        mContext = activity.getApplicationContext();
    }

    public boolean saveNewAnchor(){
        boolean result = false;
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
            result = true;
        }catch (Exception ignored){
        }finally {
            mAnchorDAO.close();
        }
        return result;
    }

    //endregion

    //region [Private methods]

    private boolean isNewAnchorDataOk(){
        boolean result = true;
        if(mActivity.getTitleValue().length()==0){
            result = false;
        }
        return result;
    }

    //endregion

}
