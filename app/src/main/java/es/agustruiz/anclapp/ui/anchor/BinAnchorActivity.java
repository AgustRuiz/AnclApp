package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.dao.AnchorDAO;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.adapter.AnchorListAdapter;

public class BinAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = BinAnchorActivity.class.getName() + "[A]";

    //region [Binded views and variables]

    @BindView(R.id.anchor_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.anchor_list_view)
    ListView mAnchorListView;

    Context mContext;
    AnchorDAO mAnchorDAO = null;
    List<Anchor> mAnchorList;
    AnchorListAdapter mAnchorListAdapter;

    //endregion

    //region [Activity methods]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_anchor_list);
        ButterKnife.bind(this);
        mContext = getApplicationContext();
        removeExpiredAnchorFromBin();
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAnchorList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //endregion

    //region [Private methods]

    private void prepareDAO() {
        if (mAnchorDAO == null) {
            mAnchorDAO = new AnchorDAO(mContext);
        }
    }

    private void refreshAnchorList() {
        prepareDAO();
        mAnchorDAO.openReadOnly();
        mAnchorList = mAnchorDAO.getAll(AnchorDAO.QUERY_GET_DELETED);
        mAnchorDAO.close();
        mAnchorListAdapter.getData().clear();
        mAnchorListAdapter.getData().addAll(mAnchorList);
        mAnchorListAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        setTitle(R.string.anchors_bin);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        mAnchorList = new ArrayList<>();
        mAnchorListAdapter = new AnchorListAdapter(mContext, getLayoutInflater(),
                R.layout.anchor_list_row, mAnchorList);

        mAnchorListView.setAdapter(mAnchorListAdapter);
        mAnchorListView.addFooterView(getLayoutInflater().inflate(R.layout.footer, mAnchorListView, false),
                null,false);
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
                        mAnchorDAO.delete(mAnchorListAdapter.getItemId(position));
                        mAnchorDAO.close();
                        mAnchorListAdapter.remove(position);
                        refreshAnchorList();
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
                    Intent intent = new Intent(mContext, SeeAnchorActivity.class);
                    intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, mAnchorList.get(position).getId());
                    startActivity(intent);
                }
            }
        });

        mSwipeRefreshLayout.setEnabled(false);
    }

    private void removeExpiredAnchorFromBin(){
        int maxDaysInBin = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(mContext).getString(
                mContext.getString(R.string.key_pref_purge_anchors),
                mContext.getString(R.string.pref_purge_anchors_default_value)));
        if(maxDaysInBin>0) {
            long maxMillisInBin = maxDaysInBin * 24 * 60 * 60 * 1000;
            long maxTimeLimit = System.currentTimeMillis() - maxMillisInBin;
            prepareDAO();
            mAnchorDAO.openReadOnly();
            List<Anchor> deletedAnchors = mAnchorDAO.getAll(AnchorDAO.QUERY_GET_DELETED);
            mAnchorDAO.close();
            for (Anchor anchor : deletedAnchors) {
                if (anchor.getDeletedTimestamp() < maxTimeLimit) {
                    mAnchorDAO.openWritable();
                    mAnchorDAO.delete(anchor);
                    mAnchorDAO.close();
                }
            }
        }
    }

    //endregion
}
