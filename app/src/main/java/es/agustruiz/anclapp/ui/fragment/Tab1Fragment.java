package es.agustruiz.anclapp.ui.fragment;

/**
 * Created by Agustin on 06/07/2015.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import es.agustruiz.anclapp.R;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab1Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_fragment, container, false);
        return v;
    }
}
