package de.test.antennapod.ui;

import android.content.SharedPreferences;
import android.test.FlakyTest;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.robotium.solo.Solo;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

/**
 * User interface tests for Bookmarks Feature
 */
public class BookmarksTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private Context context;
    private UITestUtils uiTestUtils;
    private SharedPreferences prefs;

    public BookmarksTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.context = getInstrumentation().getTargetContext();
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

    private String getActionbarTitle() {
        return ((MainActivity) solo.getCurrentActivity()).getSupportActionBar().getTitle().toString();
    }

    public void testBookmarkButton() {
        String query = "Hello Internet";

        openNavDrawer();
        solo.clickOnText("Add Podcast");
        solo.waitForDialogToOpen();
        solo.waitForView(R.id.sliding_tabs);
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.gridView);

        solo.clickOnText("Educational");
        solo.waitForDialogToOpen();
        solo.clickOnText("Subscribe");
        solo.sleep(5000);
        solo.clickOnText("Open Podcast");
        solo.waitForDialogToOpen();
        solo.clickOnText("Twelve Drummers Drumming");
        solo.waitForDialogToOpen();
        solo.clickOnText("Stream");
        solo.sleep(6000);
        solo.clickOnButton(R.id.butBookmark);
        solo.waitForDialogToOpen();


    }

}
