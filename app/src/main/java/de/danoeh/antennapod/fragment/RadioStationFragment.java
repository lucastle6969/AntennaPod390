package de.danoeh.antennapod.fragment;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.adapter.RadioStreamTabAdapter;

/**
 * Provides actions for displaying the recommended radio list and personal radio list
 */

public class RadioStationFragment extends Fragment {
    public static final String TAG = "AddRadioTabFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(R.string.radio_stream_label);

        View root = inflater.inflate(R.layout.addfeed, container, false);

        ViewPager viewPager = root.findViewById(R.id.viewpager);

        RadioStreamTabAdapter adapter = new RadioStreamTabAdapter(getChildFragmentManager(), getResources());


        viewPager.setAdapter(adapter);

        TabLayout tabLayout = root.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);

        return root;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        // So, we certainly *don't* have an options menu,
        // but unless we say we do, old options menus sometimes
        // persist.  mfietz thinks this causes the ActionBar to be invalidated
        setHasOptionsMenu(true);
    }
}
