package de.danoeh.antennapod.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.content.res.Resources;

import de.danoeh.antennapod.fragment.FyydSearchFragment;
import de.danoeh.antennapod.fragment.ItunesSearchFragment;
import de.danoeh.antennapod.fragment.URLSearchFragment;
import de.danoeh.antennapod.fragment.gpodnet.GpodderSearchFragment;

import de.danoeh.antennapod.R;


public class AddPodcastFragmentAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 4;
    final Resources resources;

    public AddPodcastFragmentAdapter(FragmentManager fm, Resources resources) {

        super(fm);
        // this allows us to access the resources without passing in the context (Activity or Service)
        this.resources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ItunesSearchFragment();
            case 1:
                return new GpodderSearchFragment();
            case 2:
                return new FyydSearchFragment();
            case 3:
                return new URLSearchFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return resources.getString(R.string.tab_itunes);
            case 1:
                return resources.getString(R.string.tab_gpodder);
            case 2:
                return resources.getString(R.string.tab_fyyd);
            case 3:
                return resources.getString(R.string.tab_url);
            default:
                return null;
        }
    }



}
