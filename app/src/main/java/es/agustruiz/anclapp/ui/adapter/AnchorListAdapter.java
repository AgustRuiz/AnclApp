package es.agustruiz.anclapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hudomju.swipe.adapter.ListViewAdapter;
import com.hudomju.swipe.adapter.ViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;

public class AnchorListAdapter extends BaseAdapter {

    public static final String LOG_TAG = AnchorListAdapter.class.getName()+"[A]";

    Context mContext;
    LayoutInflater mInflater;
    int mLayoutResourceId;
    List<Anchor> mData = new ArrayList<>();

    public AnchorListAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<Anchor> data) {
        mContext = context;
        mInflater= inflater;
        mLayoutResourceId = layoutResourceId;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Anchor getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mData.get(position).getId(); // TODO check this
    }

    public void remove(int position){
        mData.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnchorHolder holder;

        if (row == null) {
            row = mInflater.inflate(mLayoutResourceId, parent, false);
            holder = new AnchorHolder(row);
            row.setTag(holder);
        } else {
            holder = (AnchorHolder) row.getTag();
        }

        Anchor anchor = mData.get(position);

        holder.mTitle.setText(anchor.getTitle());
        holder.mIcon.setImageTintList(
                ColorStateList.valueOf(Color.parseColor(anchor.getColor())));
        holder.mDistance.setText("?km");

        return row;
    }

    public List<Anchor> getData(){
        return mData;
    }

    protected static class AnchorHolder {
        @Bind(R.id.anchor_list_icon)
        ImageView mIcon;
        @Bind(R.id.anchor_list_title)
        TextView mTitle;
        @Bind(R.id.anchor_list_distance)
        TextView mDistance;

        public AnchorHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
