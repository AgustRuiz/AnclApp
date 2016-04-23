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
import android.widget.RadioButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import es.agustruiz.anclapp.R;
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
        holder.mRadioButton.setButtonTintList(
                ColorStateList.valueOf(Color.parseColor(anchorColor.getEntryValue())));
        holder.mChecked = anchorColor.isChecked();
        holder.mRadioButton.setChecked(holder.mChecked);

        return row;
    }


    static class AnchorColorHolder {
        @Bind(R.id.color_list_view_row_radio_button)
        RadioButton mRadioButton;
        @Bind(R.id.color_list_view_row_text_view)
        TextView mTextView;
        String entryValue;
        boolean mChecked = false;

        public AnchorColorHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}