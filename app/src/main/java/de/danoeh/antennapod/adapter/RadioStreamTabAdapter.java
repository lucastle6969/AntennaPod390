package de.danoeh.antennapod.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import android.content.res.Resources;

import de.danoeh.antennapod.fragment.RadioStreamFragment;

import de.danoeh.antennapod.R;

public class RadioStreamTabAdapter extends FragmentPagerAdapter {

    private static final int NUM_PAGES = 2;
    private final Resources resources;

    public RadioStreamTabAdapter(FragmentManager fm, Resources resources) {

        super(fm);
        // this allows us to access the resources without passing in the context (Activity or Service)
        this.resources = resources;
    }


    @Override
    public Fragment getItem(int position) {
        Bundle args = new Bundle();
        switch (position) {
            case 0:
                args.putBoolean("isRecommended", false);
                RadioStreamFragment personalRadioStream = new RadioStreamFragment();
                personalRadioStream.setArguments(args);
                return personalRadioStream;
            case 1:
                args.putBoolean("isRecommended", true);
                RadioStreamFragment radioStreamFragment = new RadioStreamFragment();
                radioStreamFragment.setArguments(args);
                return radioStreamFragment;
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
                return resources.getString(R.string.my_radio_streams);
            case 1:
                return resources.getString(R.string.recommended_radio_streams);
            default:
                return null;
        }
    }
}
