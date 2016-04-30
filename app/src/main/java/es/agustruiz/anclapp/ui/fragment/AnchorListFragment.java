package es.agustruiz.anclapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.adapter.AnchorListAdapter;
import es.agustruiz.anclapp.ui.anchor.seeAnchor.SeeAnchorActivity;

public class AnchorListFragment extends Fragment {

    public static final String LOG_TAG = AnchorListFragment.class.getName()+"[A]";

    @Bind(R.id.anchor_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.anchor_list_view)
    ListView mAnchorListView;

    List<Anchor> mAnchorList;
    AnchorListAdapter mAnchorListAdapter;

    Context mContext;
    AnchorDAO mAnchorDAO;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_anchor_list, container, false);
        ButterKnife.bind(this, v);

        View footerView = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer, null, false);
        mAnchorListView.addFooterView(footerView);

        mContext = getContext();
        mAnchorDAO = new AnchorDAO(mContext);

        mAnchorList = new ArrayList<>();

        mAnchorListAdapter = new AnchorListAdapter(getContext(), inflater, R.layout.anchor_list_row, mAnchorList);
        mAnchorListView.setAdapter(mAnchorListAdapter);

        mAnchorListView.setClickable(true);
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener = new SwipeToDismissTouchListener<>(
                new ListViewAdapter(mAnchorListView),
                new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                    @Override
                    public boolean canDismiss(int position) {
                        return true;
                    }

                    @Override
                    public void onDismiss(ListViewAdapter recyclerView, int position) {
                        mAnchorDAO.openWritable();
                        mAnchorDAO.remove(mAnchorListAdapter.getItemId(position));
                        mAnchorDAO.close();
                        mAnchorListAdapter.remove(position);
                    }
                }
        );
        mAnchorListView.setOnTouchListener(touchListener);
        mAnchorListView.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        mAnchorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    // TODO launch details anchor activity
                    Intent intent = new Intent(mContext, SeeAnchorActivity.class);
                    intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, mAnchorList.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent, mContext.getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAnchorList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAnchorList();
    }

    private void refreshAnchorList() {
        mAnchorDAO.openReadOnly();
        mAnchorList = mAnchorDAO.getList();
        mAnchorDAO.close();
        mAnchorListAdapter.getData().clear();
        mAnchorListAdapter.getData().addAll(mAnchorList);
        mAnchorListAdapter.notifyDataSetChanged();
    }
}