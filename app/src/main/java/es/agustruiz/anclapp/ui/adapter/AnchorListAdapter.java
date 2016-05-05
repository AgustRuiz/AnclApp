package es.agustruiz.anclapp.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;

public class AnchorListAdapter extends BaseAdapter {

    public static final String LOG_TAG = AnchorListAdapter.class.getName() + "[A]";

    Context mContext;
    LayoutInflater mInflater;
    int mLayoutResourceId;
    List<Anchor> mData = new ArrayList<>();

    public AnchorListAdapter(Context context, LayoutInflater inflater, int layoutResourceId, List<Anchor> data) {
        mContext = context;
        mInflater = inflater;
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

    public void remove(int position) {
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
        Float distance = anchor.getDistanceInKms();
        holder.mDistance.setText((distance > 0 ? distance.toString() + mContext.getString(R.string.km_unit) : ""));
        if (anchor.isReminder()) {
            holder.mNotificationIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_notifications_black_24dp));
            holder.mNotificationIcon.setImageTintList(mContext.getColorStateList(R.color.blue500));
        }else{
            holder.mNotificationIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_notifications_off_black_24dp));
            holder.mNotificationIcon.setImageTintList(mContext.getColorStateList(R.color.grey500));
        }
        return row;
    }

    public List<Anchor> getData() {
        return mData;
    }

    protected static class AnchorHolder {
        @Bind(R.id.anchor_list_icon)
        ImageView mIcon;
        @Bind(R.id.anchor_list_title)
        TextView mTitle;
        @Bind(R.id.anchor_list_notification_icon)
        ImageView mNotificationIcon;
        @Bind(R.id.anchor_list_distance)
        TextView mDistance;

        public AnchorHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
