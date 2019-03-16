package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.test.FlakyTest;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import android.widget.RelativeLayout;

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

    public void testNavDrawPodcastOfTheDay() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.podcast_of_the_day));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.podcast_of_the_day), getActionbarTitle());
    }

    public void testGenerateNewPodcastOfTheDay() {
        String podcastOfTheDay = "";
        String generatedPodcast = "";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.podcast_of_the_day));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.podcast_of_the_day), getActionbarTitle());

        RelativeLayout relativeLayout = (RelativeLayout) solo.getView(R.id.potdlayout);
        TextView textView = (TextView) relativeLayout.getChildAt(1);


        podcastOfTheDay = textView.getText().toString();

        // This command is run twice to give robotium the time to click on the String.
        // This might be because of the relative layout being too slow.
        solo.clickOnText(solo.getString(R.string.generate_new_podcast));
        solo.clickOnText(solo.getString(R.string.generate_new_podcast));

        RelativeLayout generatedRelativeLayout = (RelativeLayout) solo.getView(R.id.potdlayout);
        TextView generatedTextView = (TextView) generatedRelativeLayout.getChildAt(1);
        generatedPodcast = generatedTextView.getText().toString();

        assertTrue(!podcastOfTheDay.equals(generatedPodcast));
    }

    public void testGoToPodcastOfTheDayPageAndSubscribe() {
        String podcast = "";
        String podcastPage = "";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.podcast_of_the_day));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.podcast_of_the_day), getActionbarTitle());

        RelativeLayout relativeLayout = (RelativeLayout) solo.getView(R.id.potdlayout);
        TextView textView = (TextView) relativeLayout.getChildAt(1);
        podcast = textView.getText().toString();

        solo.clickOnButton(solo.getString(R.string.go_to_podcast_page));
        solo.waitForActivity(OnlineFeedViewActivity.class);

        solo.waitForView(R.id.butSubscribe);
        RelativeLayout relativeLayoutPodcastPage = (RelativeLayout) solo.getView(R.id.feed_layout);
        TextView podcastPageTextView = (TextView) relativeLayoutPodcastPage.getChildAt(1);
        podcastPage = podcastPageTextView.getText().toString();
        assertEquals(podcast, podcastPage);

    }

    public void testFyydPodcastSearch() {
        String query = "TripleTwenty";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.sliding_tabs);
        solo.clickOnText(solo.getString(R.string.tab_fyyd));
        solo.clickOnText(solo.getString(R.string.search_fyyd_label));
        solo.enterText(1, query);
        solo.sendKey(Solo.ENTER);
        solo.clickOnText(solo.getString(R.string.tab_fyyd));

        ArrayList<View> views = solo.getCurrentViews();
        TextView titleTextView = null;
        for (View view : views) {
            if (view.getId() == R.id.txtvTitle) {
                TextView textView = (TextView) view;
                if (textView.getText().equals(query)) {
                    titleTextView = textView;
                    break;
                }
            }
        }
        assertNotNull(titleTextView);
        assertEquals(query, titleTextView.getText().toString());
    }

    public void testITunesSearchPodcastEpisodeCountAndGenre() {
        String query = "Hello Internet";
        String genre = "Educational Technology";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.clickOnText(solo.getString(R.string.tab_itunes));
        solo.waitForView(R.id.gridView);
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.gridView);

        GridView gridView = (GridView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);

        TextView episodeCountView = viewGroup.findViewById(R.id.txtvEpisodes);
        TextView descriptionView = viewGroup.findViewById(R.id.txtvUrl);

        assertNotNull(episodeCountView.getText());
        assertNotNull(descriptionView.getText());
        assertTrue(Integer.parseInt(episodeCountView.getText().toString())>0);
        assertEquals(descriptionView.getText().toString(),genre);
    }

    public void testURLSearchFragment(){
        String url = "http://www.hellointernet.fm/podcast?format=rss";

        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.clickOnText(solo.getString(R.string.tab_url));
        solo.waitForView(R.id.etxtFeedurl);
        solo.clearEditText(0);
        solo.enterText(0, url);
        solo.sendKey(Solo.ENTER);
        solo.clickOnButton(0);
        solo.waitForActivity(OnlineFeedViewActivity.class);
        solo.waitForView(R.id.butSubscribe);
        solo.assertCurrentActivity("Expected OnlineViewFeedActivity to be active.", OnlineFeedViewActivity.class);
    }

    public void testGpodderSearch() {
        openNavDrawer();
        solo.clickOnText("Add Podcast");
        solo.clickOnText("gPodder");
        solo.waitForView(R.id.txtvEpisodes);
        // repeating this action to avoid flaky test where Robotium clicks on iTunes tab
        solo.clickOnText("gPodder");
        solo.enterText(1, "hello");
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.txtvEpisodes);

        assertTrue(solo.searchText("Internet"));
    }

    @FlakyTest(tolerance = 3)
    public void testItunesSearch() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.add_feed_label));
        solo.clickOnText(solo.getString(R.string.tab_itunes));
        solo.waitForView(R.id.txtvEpisodes);
        solo.enterText(0,"hello");
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.txtvEpisodes);

        assertTrue(solo.searchText("Internet"));
    }

    public void testAddBookmark() {
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.podcast_of_the_day));
        solo.waitForView(android.R.id.list);
        solo.clickOnButton(solo.getString(R.string.go_to_podcast_page));
        solo.waitForActivity(OnlineFeedViewActivity.class);
        solo.waitForView(R.id.butSubscribe);
        solo.waitForActivity(OnlineFeedViewActivity.class);
        solo.clickOnButton(R.string.open_podcast);

        solo.waitForView(R.id.gridView);

        RecyclerView gridView = (RecyclerView) solo.getView(R.id.gridView);
        ViewGroup viewGroup = (ViewGroup) gridView.getChildAt(0);

    }
}

