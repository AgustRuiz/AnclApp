package es.agustruiz.anclapp.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import es.agustruiz.anclapp.R;
import es.agustruiz.anclapp.model.AnchorColor;
import es.agustruiz.anclapp.ui.adapter.ColorListAdapter;
import es.agustruiz.anclapp.ui.newAnchor.NewAnchorActivity;

public class ColorDialogFragment extends DialogFragment {

    public static final String LOG_TAG = ColorDialogFragment.class.getName() + "[A]";

    ListView mColorListView;

    ColorListAdapter mColorListAdapter;
    List<AnchorColor> mAnchorColorList = null;

    String mValueCurrentColor;
    private static final String VALUE_CURRENT_COLOR_TAG = "valueCurrentColor";

    String mTitleCurrentColor;
    private static final String TITLE_CURRENT_COLOR_TAG = "titleCurrentColor";

    public static ColorDialogFragment newInstance(String valueCurrentColor, String titleCurrentColor) {
        ColorDialogFragment colorDialogFragment = new ColorDialogFragment();
        Bundle args = new Bundle();
        args.putString(VALUE_CURRENT_COLOR_TAG, valueCurrentColor);
        args.putString(TITLE_CURRENT_COLOR_TAG, titleCurrentColor);
        colorDialogFragment.setArguments(args);
        return colorDialogFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setStyle(DialogFragment.STYLE_NORMAL, 0);
        mValueCurrentColor = getArguments().getString(VALUE_CURRENT_COLOR_TAG);
        mTitleCurrentColor = getArguments().getString(TITLE_CURRENT_COLOR_TAG);
        mAnchorColorList = getAnchorColorList(mValueCurrentColor, mTitleCurrentColor);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_color_dialog, container, false);
        mColorListView = (ListView) view.findViewById(R.id.color_list_view);

        AnchorColor[] anchorColorArray = mAnchorColorList.toArray(new AnchorColor[mAnchorColorList.size()]);
        mColorListAdapter = new ColorListAdapter(getContext(), R.layout.colors_list_preference_row, anchorColorArray);
        mColorListView.setAdapter(mColorListAdapter);
        mColorListView.setClickable(true);

        mColorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NewAnchorActivity callingActivity = (NewAnchorActivity) getActivity();
                callingActivity.setAnchorColorValues(
                        mAnchorColorList.get(position).getEntry(),
                        mAnchorColorList.get(position).getEntryValue());

                dismiss();
            }
        });
        return view;
    }

    private List<AnchorColor> getAnchorColorList(String valueCurrentColor, String titleCurrentColor) {
        // Prepare list items
        List<AnchorColor> anchorColorList = new ArrayList<>();
        String[] colorTitles = getContext().getResources().getStringArray(R.array.pref_anchors_color_titles);
        String[] colorValues = getContext().getResources().getStringArray(R.array.pref_anchors_color_values);
        boolean isCurrentColorChecked = false;
        for (int i = 0; i < colorTitles.length; ++i) {
            if(colorTitles[i].equals(titleCurrentColor) && !isCurrentColorChecked){
                anchorColorList.add(new AnchorColor(colorTitles[i], colorValues[i],true));
                isCurrentColorChecked = true;
            }else{
                anchorColorList.add(new AnchorColor(colorTitles[i], colorValues[i],false));
            }
        }
        anchorColorList.add(new AnchorColor(
                getResources().getString(R.string.default_color),
                PreferenceManager.getDefaultSharedPreferences(getContext())
                        .getString(
                                getResources().getString(R.string.key_pref_anchors_color),
                                getResources().getString(R.string.pref_anchors_color_default_value)),
                !isCurrentColorChecked));
        return anchorColorList;
    }
}
