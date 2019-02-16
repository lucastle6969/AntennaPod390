package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.util.StorageUtils;

/**
 * Provides actions for daily podcast recommendations
 */
public class PotdActivity extends AppCompatActivity {

    public static final String TAG = "PotdActivity";

    /**
     * Preset value for url text field.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // temp
        ItunesAdapter.Podcast dummy = new ItunesAdapter.Podcast("Joe Rogan", "https://is1-ssl.mzstatic.com/image/thumb/Podcasts114/v4/ec/db/85/ecdb85e6-9a4c-4231-0e0c-a2a8953940ea/mza_4877052704493588045.jpg/170x170bb-85.png",
        "http://joeroganexp.joerogan.libsynpro.com/rss", "THE JOE ROGAN SHOW", 1000);

        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Podcast of the Day");

        StorageUtils.checkStorageAvailability(this);

        Log.d(TAG, "Activity was started with url " + dummy.feedUrl);
    }
}