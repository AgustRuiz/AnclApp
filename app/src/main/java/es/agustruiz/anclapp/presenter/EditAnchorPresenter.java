package es.agustruiz.anclapp.presenter;

import android.content.Context;

import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.anchor.editAnchor.EditAnchorActivity;

public class EditAnchorPresenter implements Presenter {

    EditAnchorActivity mActivity = null;
    Context mContext = null;
    AnchorDAO mAnchorDAO;
    Anchor mAnchor;

    //region [Public methods]

    public  EditAnchorPresenter(EditAnchorActivity activity){
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
    }

    public Anchor getAnchor(long id){
        if(mAnchor==null){
            prepareDAO();
            mAnchorDAO.openReadOnly();
            mAnchor = mAnchorDAO.get(id);
            mAnchorDAO.close();
        }
        return mAnchor;
    }

    //endregion

    //region [Overriden methods]

    @Override
    public void showMessage(String message) {

    }

    //endregion

    //region [Private methods]

    protected void prepareDAO(){
        if(mAnchorDAO==null)
            mAnchorDAO = new AnchorDAO(mContext);
    }

    //endregion
}
