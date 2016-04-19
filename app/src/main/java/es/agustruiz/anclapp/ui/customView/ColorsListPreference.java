package es.agustruiz.anclapp.ui.customView;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;

import es.agustruiz.anclapp.R;

import static android.content.SharedPreferences.*;

/**
 * Custom Colors ListPreference.
 * Idea from http://stackoverflow.com/questions/4549746/custom-row-in-a-listpreference
 *
 * Use only if entry values are colors in hexadecimal format (#123456)
 */

public class ColorsListPreference extends ListPreference {

    ColorsListPreferenceAdapter colorsListPreferenceAdapter = null;
    Context mContext;
    private LayoutInflater mInflater;
    CharSequence[] entries;
    CharSequence[] entryValues;
    ArrayList<RadioButton> rButtonList;
    SharedPreferences prefs;
    Editor editor;
    String keyPref;

    public ColorsListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mInflater = LayoutInflater.from(context);
        rButtonList = new ArrayList<>();
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = prefs.edit();
        keyPref = this.getKey();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        entries = getEntries();
        entryValues = getEntryValues();

        if (entries == null || entryValues == null || entries.length != entryValues.length) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array which are both the same length");
        }

        colorsListPreferenceAdapter = new ColorsListPreferenceAdapter(mContext);

        builder.setAdapter(colorsListPreferenceAdapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
    }

    private class ColorsListPreferenceAdapter extends BaseAdapter {
        public ColorsListPreferenceAdapter(Context context) {
        }

        public int getCount() {
            return entries.length;
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {
            View row = convertView;
            final CustomHolder holder;
            final String value = entryValues[position].toString();
            if (row == null) {
                row = mInflater.inflate(R.layout.custom_list_preference_row, parent, false);
                holder = new CustomHolder(row, position);
                checkRadioButton(value, holder);
                row.setClickable(true);
                row.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        editor.putString(keyPref, holder.color);
                        editor.commit();
                        getDialog().dismiss();
                    }
                });
                row.setTag(holder);
            } else {
                holder = (CustomHolder) convertView.getTag();
                holder.updateRow(position);
                checkRadioButton(value, holder);
            }
            return row;
        }

        private void checkRadioButton(String value, CustomHolder holder) {
            if (value.equals(prefs.getString(keyPref, getValue()))) {
                holder.rButton.setChecked(true);
            } else {
                holder.rButton.setChecked(false);
            }
        }

        class CustomHolder {
            private TextView text = null;
            private RadioButton rButton = null;
            private String color = null;

            CustomHolder(View row, int position) {
                text = (TextView) row.findViewById(R.id.custom_list_view_row_text_view);
                rButton = (RadioButton) row.findViewById(R.id.custom_list_view_row_radio_button);
                updateRow(position);
                rButtonList.add(rButton);
            }

            public void updateRow(int position) {
                color = entryValues[position].toString();
                text.setText(entries[position]);
                rButton.setButtonTintList(ColorStateList.valueOf(Color.parseColor(color)));
            }
        }
    }
}
