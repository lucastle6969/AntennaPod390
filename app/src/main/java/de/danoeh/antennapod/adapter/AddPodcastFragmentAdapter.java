package de.danoeh.antennapod.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import de.danoeh.antennapod.fragment.FyydSearchFragment;
import de.danoeh.antennapod.fragment.ItunesSearchFragment;
import de.danoeh.antennapod.fragment.gpodnet.GpodnetMainFragment;

public class AddPodcastFragmentAdapter extends FragmentPagerAdapter {

    private Context mContext;
    private Resources resources;

    public AddPodcastFragmentAdapter(FragmentManager fm, Resources resources) {
        super(fm);
        this.resources = resources;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new ItunesSearchFragment();
            case 1:
                return new GpodnetMainFragment();
            case 2:
                return new FyydSearchFragment();
            case 3:
                // TODO MAKE URL FRAGMENT and RETURN IT HERE!
                return new Fragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        switch (position) {
            case 0:
                return "itunes";
            case 1:
                return "gPodder";
            case 2:
                return "fyyd";
            case 3:
                return "url";
            default:
                return null;
        }
    }



}
