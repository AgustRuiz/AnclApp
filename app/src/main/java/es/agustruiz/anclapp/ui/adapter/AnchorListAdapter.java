package es.agustruiz.anclapp.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.Anchor;

public class AnchorListAdapter extends ArrayAdapter<Anchor>{

    public static final String LOG_TAG = AnchorListAdapter.class.getName()+"[A]";

    Context mContext;
    int mLayoutResourceId;
    Anchor mData[] = null;

    public AnchorListAdapter(Context context, int layoutResourceId, Anchor[] data) {
        super(context, layoutResourceId, data);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnchorHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new AnchorHolder(row);
            row.setTag(holder);
        } else {
            holder = (AnchorHolder) row.getTag();
        }

        Anchor anchor = mData[position];

        holder.mTitle.setText(anchor.getTitle());
        holder.mIcon.setImageTintList(
                ColorStateList.valueOf(Color.parseColor(anchor.getColor())));
        holder.mDistance.setText("?km");

        return row;
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
