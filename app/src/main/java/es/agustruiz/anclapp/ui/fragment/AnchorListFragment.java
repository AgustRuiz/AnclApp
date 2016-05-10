package es.agustruiz.anclapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import es.agustruiz.anclapp.event.Event;
import es.agustruiz.anclapp.event.EventsUtil;
import es.agustruiz.anclapp.event.IEventHandler;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.MainActivity;
import es.agustruiz.anclapp.ui.adapter.AnchorListAdapter;
import es.agustruiz.anclapp.ui.anchor.SeeAnchorActivity;

public class AnchorListFragment extends Fragment {

    public static final String LOG_TAG = AnchorListFragment.class.getName() + "[A]";

    @Bind(R.id.anchor_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.anchor_list_view)
    ListView mAnchorListView;

    Context mContext;
    AnchorDAO mAnchorDAO;
    List<Anchor> mAnchorList;
    AnchorListAdapter mAnchorListAdapter;

    //region [Fragment methods]

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_anchor_list, container, false);
        ButterKnife.bind(this, v);
        mContext = getContext();
        initialize(inflater);
        registryEventListeners();
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshAnchorList();
    }

    //endregion

    //region [Private methods]

    private void initialize(LayoutInflater inflater){
        mAnchorDAO = new AnchorDAO(mContext);

        mAnchorList = new ArrayList<>();
        mAnchorListAdapter = new AnchorListAdapter(getContext(), inflater, R.layout.anchor_list_row, mAnchorList);

        mAnchorListView.setAdapter(mAnchorListAdapter);
        mAnchorListView.addFooterView(((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer, null, false));
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
                        EventsUtil.getInstance().refreshAnchorMarkers();
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

        /**/
        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent, mContext.getTheme()));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshAnchorList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });/**/
    }

    private void refreshAnchorList(){
        String titleQuery = MainActivity.getSearchString();
        mAnchorDAO.openReadOnly();
        if(titleQuery.length()>0){
            mAnchorList = mAnchorDAO.getByTitle(titleQuery);
        }else{
            mAnchorList = mAnchorDAO.getAll();
        }
        mAnchorDAO.close();
        mAnchorListAdapter.getData().clear();
        mAnchorListAdapter.getData().addAll(mAnchorList);
        mAnchorListAdapter.notifyDataSetChanged();
    }

    private void registryEventListeners() {
        EventsUtil mEventsUtil = EventsUtil.getInstance();
        mEventsUtil.addEventListener(EventsUtil.NOTIFIY_CURRENT_LOCATION_CHANGED,
                new IEventHandler() {
                    @Override
                    public void callback(Event event) {
                        refreshAnchorList();
                    }
                }
        );
        mEventsUtil.addEventListener(EventsUtil.REFRESH_ANCHOR_LIST, new IEventHandler() {
            @Override
            public void callback(Event event) {
                refreshAnchorList();
            }
        });
    }

    //endregion
}