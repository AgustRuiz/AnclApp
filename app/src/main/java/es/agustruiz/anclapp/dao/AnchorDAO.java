package es.agustruiz.anclapp.dao;

import java.util.ArrayList;
import java.util.List;

import es.agustruiz.anclapp.model.Anchor;

public class AnchorDAO {

    public static final String LOG_TAG = AnchorDAO.class.getName() + "[A]";

    private static AnchorDAO ourInstance = new AnchorDAO();
    private List<Anchor> mAnchorList = null;

    //region [Singleton constructor]

    public static AnchorDAO getInstance() {
        return ourInstance;
    }

    //endregion

    //region [Public methods]

    public List<Anchor> getList() {
        if (mAnchorList == null) {
            mAnchorList = new ArrayList<>();
            populateTestAnchorList();
        }
        return mAnchorList;
    }

    public Anchor get(long id) {
        for (Anchor anchor : mAnchorList) {
            if (anchor.getId() == id) {
                return anchor;
            }
        }
        return null;
    }

    public boolean remove(long id){
        for(Anchor anchor : mAnchorList){
            if(anchor.getId() == id){
                mAnchorList.remove(anchor);
                return true;
            }
        }
        return false;
    }

    //endregion

    //region [Private methods]

    // TODO dummy items
    private void populateTestAnchorList() {
        mAnchorList.clear();
        mAnchorList.add(new Anchor(0L, 0.0, 0.0, "Anchor 1", "Anchor 1 description", "#d50000", true));
        mAnchorList.add(new Anchor(1L, 1.0, 1.0, "Anchor 2", "Anchor 2 description", "#f4511e", true));
        mAnchorList.add(new Anchor(2L, 2.0, 2.0, "Anchor 3", "Anchor 3 description", "#f6bf26", true));
        mAnchorList.add(new Anchor(3L, 3.0, 3.0, "Anchor 4", "Anchor 4 description", "#0b8043", true));
        mAnchorList.add(new Anchor(4L, 4.0, 4.0, "Anchor 5", "Anchor 5 description", "#33b679", true));
        mAnchorList.add(new Anchor(5L, 5.0, 5.0, "Anchor 6", "Anchor 6 description", "#039be5", true));
        mAnchorList.add(new Anchor(6L, 6.0, 6.0, "Anchor 7", "Anchor 7 description", "#3f51b5", true));
        mAnchorList.add(new Anchor(7L, 7.0, 7.0, "Anchor 8", "Anchor 8 description", "#7986cb", true));
        mAnchorList.add(new Anchor(8L, 8.0, 8.0, "Anchor 9", "Anchor 9 description", "#8e24aa", true));
        mAnchorList.add(new Anchor(9L, 9.0, 9.0, "Anchor 10", "Anchor 10 description", "#e67c73", true));
        mAnchorList.add(new Anchor(10L, 10.0, 10.0, "Anchor 11", "Anchor 11 description", "#616161", true));
    }

    //endregion

}
