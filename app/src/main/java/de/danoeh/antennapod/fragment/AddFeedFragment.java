package de.danoeh.antennapod.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.AddPodcastFragmentAdapter;

/**
 * Provides actions for adding new podcast subscriptions
 */
public class AddFeedFragment extends Fragment {

    public static final String TAG = "AddFeedFragment";
    private TabLayout tabLayout;
    private ViewPager viewPager;

    /**
     * Preset value for url text field.
     */
    private static final String ARG_FEED_URL = "feedurl";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.addfeed, container, false);
        viewPager = root.findViewById(R.id.viewpager);

        AddPodcastFragmentAdapter adapter = new AddPodcastFragmentAdapter(getChildFragmentManager(), getResources());

        viewPager.setAdapter(adapter);
        tabLayout = root.findViewById(R.id.sliding_tabs);
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
