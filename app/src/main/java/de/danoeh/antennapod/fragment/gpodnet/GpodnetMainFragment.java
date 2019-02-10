package de.danoeh.antennapod.fragment.gpodnet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import de.danoeh.antennapod.R;

/**
 * Main navigation hub for gpodder.net podcast directory
 */
public class GpodnetMainFragment extends Fragment {

    private static final String TAG = "GpodnetMainFragment";

    private static final String PREF_LAST_TAB_POSITION = "tab_position";
    // private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View root = inflater.inflate(R.layout.pager_fragment_b, container, false);

        viewPager = (ViewPager)root.findViewById(R.id.viewpager);
        GpodnetPagerAdapter pagerAdapter = new GpodnetPagerAdapter(getChildFragmentManager(), getResources());
        viewPager.setAdapter(pagerAdapter);

        return root;
    }

    public class GpodnetPagerAdapter extends FragmentPagerAdapter {


        private static final int NUM_PAGES = 2;
        private static final int POS_TOPLIST = 0;
        private static final int POS_TAGS = 1;
        private static final int POS_SUGGESTIONS = 2;

        final Resources resources;

        public GpodnetPagerAdapter(FragmentManager fm, Resources resources) {
            super(fm);
            this.resources = resources;
        }

        @Override
        public Fragment getItem(int i) {
                    return new PodcastTopListFragment();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case POS_TAGS:
                    return getString(R.string.gpodnet_taglist_header);
                case POS_TOPLIST:
                    return getString(R.string.gpodnet_toplist_header);
                case POS_SUGGESTIONS:
                    return getString(R.string.gpodnet_suggestions_header);
                default:
                    return super.getPageTitle(position);
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
