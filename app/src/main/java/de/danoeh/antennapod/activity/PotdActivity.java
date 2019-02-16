package de.danoeh.antennapod.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.activity.OpmlImportFromPathActivity;
import de.danoeh.antennapod.adapter.itunes.ItunesAdapter;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.fragment.gpodnet.GpodnetMainFragment;

/**
 * Provides actions for daily podcast recommendations
 */
public class PotdActivity extends AppCompatActivity {

    public static final String TAG = "PotdFragment";

    /**
     * Preset value for url text field.
     */
    private static final String ARG_FEED_URL = "feedurl";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(UserPreferences.getTheme());
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.potd);

    }

}