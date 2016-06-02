package es.agustruiz.anclapp.ui.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.SystemUtils;
import es.agustruiz.anclapp.model.AnchorColor;

public class ColorListAdapter extends ArrayAdapter<AnchorColor> {

    public static final String LOG_TAG = ColorListAdapter.class.getName()+"[A]";

    Context mContext;
    int mLayoutResourceId;
    AnchorColor mData[] = null;

    public ColorListAdapter(Context context, int layoutResourceId, AnchorColor[] data) {
        super(context, layoutResourceId, data);
        mContext = context;
        mLayoutResourceId = layoutResourceId;
        mData = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        AnchorColorHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
            row = inflater.inflate(mLayoutResourceId, parent, false);
            holder = new AnchorColorHolder(row);
            row.setTag(holder);
        } else {
            holder = (AnchorColorHolder) row.getTag();
        }

        AnchorColor anchorColor = mData[position];

        holder.entryValue = anchorColor.getEntryValue();
        holder.mTextView.setText(anchorColor.getEntry());

        holder.mChecked = anchorColor.isChecked();
        if(holder.mChecked){
            holder.mColorIcon.setImageDrawable(SystemUtils.getDrawableFromResources(
                    mContext, R.drawable.ic_color_item_solid_24dp));
        }else{
            holder.mColorIcon.setImageDrawable(SystemUtils.getDrawableFromResources(
                    mContext, R.drawable.ic_color_item_empty_24dp));
        }
        SystemUtils.tintDrawable(holder.mColorIcon.getDrawable(), anchorColor.getEntryValue());

        return row;
    }


    static class AnchorColorHolder {
        @BindView(R.id.color_list_view_row_color_icon)
        ImageView mColorIcon;
        @BindView(R.id.color_list_view_row_text_view)
        TextView mTextView;
        String entryValue;
        boolean mChecked = false;

        public AnchorColorHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}