package es.agustruiz.anclapp.ui.anchor;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

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

public class BinAnchorActivity extends AppCompatActivity {

    public static final String LOG_TAG = BinAnchorActivity.class.getName() + "[A]";

    //region [Binded views and variables]

    @Bind(R.id.anchor_list_swipe_refresh)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.anchor_list_view)
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
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshAnchorList();
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
        mAnchorList = mAnchorDAO.getAll(); // TODO Replace by "getDeleted" method
        mAnchorDAO.close();
        mAnchorListAdapter.getData().clear();
        mAnchorListAdapter.getData().addAll(mAnchorList);
        mAnchorListAdapter.notifyDataSetChanged();
    }

    private void initialize() {
        mAnchorList = new ArrayList<>();
        mAnchorListAdapter = new AnchorListAdapter(mContext, getLayoutInflater(),
                R.layout.anchor_list_row, mAnchorList);

        mAnchorListView.setAdapter(mAnchorListAdapter);
        /*mAnchorListView.addFooterView(((LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                .inflate(R.layout.footer, null, false));/**/
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
                    /*Intent intent = new Intent(mContext, SeeAnchorActivity.class);
                    intent.putExtra(SeeAnchorActivity.ANCHOR_ID_INTENT_TAG, mAnchorList.get(position).getId());
                    startActivity(intent);/**/
                    Toast.makeText(mContext, "See deleted anchor \""
                                    + mAnchorList.get(position).getTitle() + "\"",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        mSwipeRefreshLayout.setEnabled(false);
    }

    //endregion
}
