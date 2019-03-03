package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;
import android.test.FlakyTest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.robotium.solo.Solo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.activity.OnlineFeedViewActivity;
import de.danoeh.antennapod.activity.PreferenceActivity;
import de.danoeh.antennapod.core.feed.Feed;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.PodDBAdapter;
import de.danoeh.antennapod.fragment.DownloadsFragment;
import de.danoeh.antennapod.fragment.EpisodesFragment;
import de.danoeh.antennapod.fragment.PlaybackHistoryFragment;
import de.danoeh.antennapod.fragment.QueueFragment;
import de.danoeh.antennapod.preferences.PreferenceController;

/**
 * User interface tests for MainActivity
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private UITestUtils uiTestUtils;

    private SharedPreferences prefs;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Context context = getInstrumentation().getTargetContext();
        uiTestUtils = new UITestUtils(context);
        uiTestUtils.setup();

        // create new database
        PodDBAdapter.init(context);
        PodDBAdapter.deleteDatabase();
        PodDBAdapter adapter = PodDBAdapter.getInstance();
        adapter.open();
        adapter.close();

        // override first launch preference
        // do this BEFORE calling getActivity()!
        prefs = getInstrumentation().getTargetContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MainActivity.PREF_IS_FIRST_LAUNCH, false).commit();

        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    protected void tearDown() throws Exception {
        uiTestUtils.tearDown();
        solo.finishOpenedActivities();

        PodDBAdapter.deleteDatabase();

        // reset preferences
        prefs.edit().clear().commit();

        super.tearDown();
    }

    private void openNavDrawer() {
        solo.clickOnImageButton(0);
        getInstrumentation().waitForIdleSync();
    }

    public void testAddFeed() throws Exception {
        uiTestUtils.addHostedFeedData();
        final Feed feed = uiTestUtils.hostedFeeds.get(0);
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.clickOnButton(0);
        solo.enterText(0, feed.getDownload_url());
        solo.clickOnButton(solo.getString(R.string.confirm_label));
        solo.waitForActivity(OnlineFeedViewActivity.class);
        solo.waitForView(R.id.butSubscribe);
        assertEquals(solo.getString(R.string.subscribe_label), solo.getButton(0).getText().toString());
        solo.clickOnButton(0);
        solo.waitForText(solo.getString(R.string.subscribed_label));
    }

    @FlakyTest(tolerance = 3)
    public void testClickNavDrawer() throws Exception {
        uiTestUtils.addLocalFeedData(false);

        UserPreferences.setHiddenDrawerItems(new ArrayList<>());

        // queue
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.queue_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.queue_label), getActionbarTitle());

        // episodes
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.episodes_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.episodes_label), getActionbarTitle());

        // Subscriptions
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.subscriptions_label));
        solo.waitForView(R.id.subscriptions_grid);
        assertEquals(solo.getString(R.string.subscriptions_label), getActionbarTitle());

        // downloads
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.downloads_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.downloads_label), getActionbarTitle());

        // playback history
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.playback_history_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.playback_history_label), getActionbarTitle());

        // add podcast
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.waitForView(R.id.txtvFeedurl);
        assertEquals(solo.getString(R.string.add_feed_label), getActionbarTitle());

        // podcasts
        ListView list = (ListView) solo.getView(R.id.nav_list);
        for (int i = 0; i < uiTestUtils.hostedFeeds.size(); i++) {
            Feed f = uiTestUtils.hostedFeeds.get(i);
            openNavDrawer();
            solo.scrollListToLine(list, i);
            solo.clickOnText(f.getTitle());
            solo.waitForView(android.R.id.list);
            assertEquals("", getActionbarTitle());
        }
    }

    private String getActionbarTitle() {
        return ((MainActivity) solo.getCurrentActivity()).getSupportActionBar().getTitle().toString();
    }

    @SuppressWarnings("unchecked")
    @FlakyTest(tolerance = 3)
    public void testGoToPreferences() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.settings_label));
        solo.waitForActivity(PreferenceActivity.class);
    }

    //Test to verify that we can properly hide elements from the navigation drawer and that the separator is still in place
    public void testDrawerPreferencesHideSomeElements(){
        UserPreferences.setHiddenDrawerItems(new ArrayList<>());

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.settings_label));
        solo.waitForActivity(PreferenceActivity.class);
        solo.clickOnText(solo.getString(R.string.user_interface_label));
        solo.clickOnText(solo.getString(R.string.pref_nav_drawer_items_title));
        solo.waitForDialogToOpen();
        solo.clickOnText(solo.getString(R.string.episodes_label));
        solo.clickOnText(solo.getString(R.string.downloads_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.waitForDialogToClose();
        solo.clickOnImageButton(0);
        solo.clickOnImageButton(0);
        openNavDrawer();

        List<String> hidden = UserPreferences.getHiddenDrawerItems();
        assertEquals(2, hidden.size());
        assertTrue(hidden.contains(EpisodesFragment.TAG));
        assertTrue(hidden.contains(DownloadsFragment.TAG));
    }

    //Test to verify that we can unhide hidden elements and that the separator stays in place
    public void testDrawerPreferencesUnhideSomeElements(){
        List<String> hidden = Arrays.asList(EpisodesFragment.TAG, DownloadsFragment.TAG);
        UserPreferences.setHiddenDrawerItems(hidden);

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.settings_label));
        solo.waitForActivity(PreferenceActivity.class);
        solo.clickOnText(solo.getString(R.string.user_interface_label));
        solo.clickOnText(solo.getString(R.string.pref_nav_drawer_items_title));
        solo.waitForDialogToOpen();
        solo.clickOnText(solo.getString(R.string.downloads_label));
        solo.clickOnText(solo.getString(R.string.playback_history_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.waitForDialogToClose();
        solo.clickOnImageButton(0);
        solo.clickOnImageButton(0);
        openNavDrawer();

        hidden = UserPreferences.getHiddenDrawerItems();
        assertEquals(2, hidden.size());
        assertTrue(hidden.contains(EpisodesFragment.TAG));
        assertTrue(hidden.contains(PlaybackHistoryFragment.TAG));
    }

    //Test to verify that all elements can be hidden without breaking the app and that the separator is not present when no navigation drawer fragments are there
    public void testDrawerPreferencesHideAllElements(){
        UserPreferences.setHiddenDrawerItems(new ArrayList<>());
        String[] titles = getInstrumentation().getTargetContext().getResources().getStringArray(R.array.nav_drawer_titles);

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.settings_label));
        solo.waitForActivity(PreferenceActivity.class);
        solo.clickOnText(solo.getString(R.string.user_interface_label));
        solo.clickOnText(solo.getString(R.string.pref_nav_drawer_items_title));
        solo.waitForDialogToOpen();
        for (String title : titles) {
            solo.clickOnText(title);
        }
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.waitForDialogToClose();
        solo.clickOnImageButton(0);
        solo.clickOnImageButton(0);
        openNavDrawer();

        List<String> hidden = UserPreferences.getHiddenDrawerItems();
        assertEquals(titles.length, hidden.size());
        for (String tag : MainActivity.NAV_DRAWER_TAGS) {
            assertTrue(hidden.contains(tag));
        }
    }

    //Test to verify that we can hide the current element and the separator stays in place
    public void testDrawerPreferencesHideCurrentElement(){
        UserPreferences.setHiddenDrawerItems(new ArrayList<>());

        openNavDrawer();
        String downloads = solo.getString(R.string.downloads_label);
        solo.clickOnText(downloads);
        solo.waitForView(android.R.id.list);
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.settings_label));
        solo.waitForActivity(PreferenceActivity.class);
        solo.clickOnText(solo.getString(R.string.user_interface_label));
        solo.clickOnText(solo.getString(R.string.pref_nav_drawer_items_title));
        solo.waitForDialogToOpen();
        solo.clickOnText(downloads);
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.waitForDialogToClose();
        solo.clickOnImageButton(0);
        solo.clickOnImageButton(0);
        openNavDrawer();

        List<String> hidden = UserPreferences.getHiddenDrawerItems();
        assertEquals(1, hidden.size());
        assertTrue(hidden.contains(DownloadsFragment.TAG));
    }

    public void testFyydPodcastSearch() {
        String query = "TripleTwenty";
        String description = "TripleTwenty ist der Rollenspielpodcast einer Anfängergruppe und lädt alle zum mithören ein, die sich für Pen&Paper wie \"Das Schwarze Auge\" oder \"Dungeons & Dragons\" interessieren - oder nur mal reinschnuppern wollen.";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.sliding_tabs);
        solo.clickOnText(solo.getString(R.string.tab_fyyd));
        solo.waitForView(R.id.action_search);
        solo.clickOnView(solo.getView(R.id.action_search));
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.clickOnText(solo.getString(R.string.tab_fyyd));
        solo.waitForText(query);
        solo.waitForView(R.id.txtvUrl);

        TextView descTextView = (TextView) solo.getView(R.id.txtvUrl);

        assertEquals(description, descTextView.getText());
    }

    public void testFyydPodcastEpisodesAndDescription() {
        String query = "TripleTwenty";
        String description = "TripleTwenty ist der Rollenspielpodcast einer Anfängergruppe und lädt alle zum mithören ein, die sich für Pen&Paper wie \"Das Schwarze Auge\" oder \"Dungeons & Dragons\" interessieren - oder nur mal reinschnuppern wollen.";
        String numOfEpisodes = "126";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.pressSpinnerItem(0, 2);
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.action_search);
        solo.clickOnView(solo.getView(R.id.action_search));
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForDialogToOpen();

        GridView gridView = (GridView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);
        TextView descriptionView = viewGroup.findViewById(R.id.txtvUrl);
        TextView episodesView = viewGroup.findViewById(R.id.txtvEpisodes);

        assertEquals(numOfEpisodes, episodesView.getText());
        assertEquals(description, descriptionView.getText());
    }

    public void testITunesTopPodcastEpisodeDescriptionAndHiddenCount() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.pressSpinnerItem(0, 1);
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.action_search);

        GridView gridView = (GridView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);

        TextView descriptionView = viewGroup.findViewById(R.id.txtvUrl);
        ImageView episodesIconView = viewGroup.findViewById(R.id.imgFeed);
        TextView episodesView = viewGroup.findViewById(R.id.txtvEpisodes);

        assertNotNull(descriptionView.getText());
        assertFalse(descriptionView.getText().toString().equals(""));
        assertEquals(View.GONE, episodesIconView.getVisibility());
        assertEquals(View.GONE, episodesView.getVisibility());
    }

    public void testITunesSearchPodcastEpisodeCountAndGenre() {
        String query = "Hello Internet";
        String numOfEpisodes = "100";
        String genre = "Educational Technology";


        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.pressSpinnerItem(0, 1);
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.action_search);
        solo.clickOnView(solo.getView(R.id.action_search));
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForDialogToOpen();

        GridView gridView = (GridView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);

        TextView episodeCountView = viewGroup.findViewById(R.id.txtvEpisodes);
        TextView descriptionView = viewGroup.findViewById(R.id.txtvUrl);

        assertNotNull(episodeCountView.getText());
        assertNotNull(descriptionView.getText());
        assertEquals(episodeCountView.getText().toString(),numOfEpisodes);
        assertEquals(descriptionView.getText().toString(),genre);
    }

    public void testGpodderPodcastDescription() {
        String description = "Witty, irreverent look at the world through scientists' eyes. With Brian Cox\n" +
                "and Robin Ince";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.pressSpinnerItem(0, 3);
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.txtvEpisodes);

        GridView gridView = (GridView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);
        TextView descriptionView = viewGroup.findViewById(R.id.txtvDescription);

        assertEquals(description, descriptionView.getText());
    }
}
