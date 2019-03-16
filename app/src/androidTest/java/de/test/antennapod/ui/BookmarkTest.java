package de.test.antennapod.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.test.ActivityInstrumentationTestCase2;

import com.robotium.solo.Solo;

import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.service.playback.PlaybackService;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

public class BookmarkTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private static final String TAG = PlaybackTest.class.getSimpleName();
    private static final int EPISODES_DRAWER_LIST_INDEX = 1;
    private static final int QUEUE_DRAWER_LIST_INDEX = 0;

    private Solo solo;
    private UITestUtils uiTestUtils;

    private Context context;

    public BookmarkTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        context = getInstrumentation().getTargetContext();

        PodDBAdapter.init(context);
        PodDBAdapter.deleteDatabase();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit()
                .clear()
                .putBoolean(UserPreferences.PREF_UNPAUSE_ON_HEADSET_RECONNECT, false)
                .putBoolean(UserPreferences.PREF_PAUSE_ON_HEADSET_DISCONNECT, false)
                .commit();

        solo = new Solo(getInstrumentation(), getActivity());

        uiTestUtils = new UITestUtils(context);
        uiTestUtils.setup();

        // create database
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.close();
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        uiTestUtils.tearDown();

        // shut down playback service
//        skipEpisode();
        context.sendBroadcast(new Intent(PlaybackService.ACTION_SHUTDOWN_PLAYBACK_SERVICE));

        super.tearDown();
    }
}
