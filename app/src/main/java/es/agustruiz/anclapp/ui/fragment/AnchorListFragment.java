package es.agustruiz.anclapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.model.AnchorColor;
import es.agustruiz.anclapp.ui.adapter.AnchorListAdapter;
import es.agustruiz.anclapp.ui.adapter.ColorListAdapter;

public class AnchorListFragment extends Fragment {

    @Bind(R.id.anchor_list_view)
    ListView mAnchorListView;

    List<Anchor> mAnchorList;
    AnchorListAdapter mAnchorListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_anchor_list, container, false);
        ButterKnife.bind(this, v);

        mAnchorList = new ArrayList<>();
        populateAnchorList();

        Anchor[] anchorArray = mAnchorList.toArray(new Anchor[mAnchorList.size()]);
        mAnchorListAdapter = new AnchorListAdapter(getContext(), R.layout.anchor_list_row, anchorArray);
        mAnchorListView.setAdapter(mAnchorListAdapter);
        mAnchorListView.setClickable(true);

        return v;
    }

    //region [Private methods]

    // TODO this is test objects
    private void populateAnchorList() {
        mAnchorList.clear();
        mAnchorList.add(new Anchor(0.0, 0.0, "Tomate", "Descripción tomate", "#d50000", true));
        mAnchorList.add(new Anchor(1.0, 1.0, "Mandarina", "Descripción mandarina", "#f4511e", true));
        mAnchorList.add(new Anchor(2.0, 2.0, "Plátano", "Descripción plátano", "#f6bf26", true));
        mAnchorList.add(new Anchor(3.0, 3.0, "Albahaca", "Descripción albahaca", "#0b8043", true));
        mAnchorList.add(new Anchor(4.0, 4.0, "Salvia", "Descripción salvia", "#33b679", true));
        mAnchorList.add(new Anchor(5.0, 5.0, "Pavo real", "Descripción pavo real", "#039be5", true));
        mAnchorList.add(new Anchor(6.0, 6.0, "Arándano", "Descripción arándano", "#3f51b5", true));
        mAnchorList.add(new Anchor(7.0, 7.0, "Lavanda", "Descripción lavanda", "#7986cb", true));
        mAnchorList.add(new Anchor(8.0, 8.0, "Uva negra", "Descripción uva negra", "#8e24aa", true));
        mAnchorList.add(new Anchor(9.0, 9.0, "Flamenco", "Descripción flamenco", "#e67c73", true));
        mAnchorList.add(new Anchor(10.0, 10.0, "Grafito", "Descripción grafito", "#616161", true));
    }

    //endregion
}