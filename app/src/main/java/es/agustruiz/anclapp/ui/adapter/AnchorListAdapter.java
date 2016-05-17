package es.agustruiz.anclapp.ui.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.model.Anchor;
import es.agustruiz.anclapp.ui.MainActivity;

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

        //holder.mTitle.setText(anchor.getTitle());
        holder.mTitle.setText(getHightligthedTitle(anchor.getTitle()), TextView.BufferType.SPANNABLE);


        holder.mIcon.setImageTintList(
                ColorStateList.valueOf(Color.parseColor(anchor.getColor())));
        Float distance = anchor.getDistanceInKms();
        if (anchor.isReminder()) {
            holder.mNotificationIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_notifications_black_24dp));
            holder.mNotificationIcon.setImageTintList(mContext.getColorStateList(R.color.blue500));
        } else {
            holder.mNotificationIcon.setImageDrawable(mContext.getDrawable(R.drawable.ic_notifications_off_black_24dp));
            holder.mNotificationIcon.setImageTintList(mContext.getColorStateList(R.color.grey500));
        }
        if(anchor.getDeletedTimestamp()>0) {
            // Deleted anchor
            holder.mDeletedDate.setText(mContext.getString(R.string.msg_deleted_on,
                    SystemUtils.getTime(mContext, anchor.getDeletedTimestamp()),
                    SystemUtils.getDate(mContext, anchor.getDeletedTimestamp())));
            holder.mDistance.setText("");
        }else{
            // Active anchor
            holder.mDeletedDate.setText("");
            holder.mDistance.setText((distance > 0 ? distance.toString() + mContext.getString(R.string.km_unit) : ""));
        }
        return row;
    }

    public List<Anchor> getData() {
        return mData;
    }

    private Spannable getHightligthedTitle(String origString) {
        String[] highlightLowerString = MainActivity.getSearchString().trim().toLowerCase().split(" ");
        String origLowerString = origString.toLowerCase();
        Spannable spannable = new SpannableString(origString);

        for(String subString : highlightLowerString) {
            if (subString.length() > 0) {
                int from = origLowerString.indexOf(subString);
                int to = Math.min(from + subString.length(), origString.length());
                spannable.setSpan(
                        new ForegroundColorSpan(mContext.getColor(R.color.colorAccent)),
                        from,
                        to,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannable;
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
        @Bind(R.id.anchor_list_deleted_date)
        TextView mDeletedDate;

        public AnchorHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
