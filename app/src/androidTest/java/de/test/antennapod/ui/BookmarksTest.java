package de.test.antennapod.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.test.ActivityInstrumentationTestCase2;
import android.view.Display;
import android.widget.CheckBox;
import android.widget.ImageButton;

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

    public void testBookmarkButton() {
        selectAndClickOnBookmarkButton();
        assertTrue(solo.searchText("Set a bookmark", true));

    }

    public void testPlaybackButton() {
        selectAndClickOnBookmarkButton();
        solo.clickOnText("Confirm");

        scrollingToBookmarkTab();

        ImageButton PlaybackBookmarkButton = (ImageButton) solo.getView("imgBookmarkPlay");
        solo.clickOnView(PlaybackBookmarkButton);
        solo.waitForDialogToOpen(5000);

        assertEquals(solo.getString(R.id.txtvTimestamp), solo.getString(R.id.txtvPosition));
    }

    public void testAddBookmark() {
        selectAndClickOnBookmarkButton();
        solo.clickOnText("Confirm");

        scrollingToBookmarkTab();

        //Verify if the first bookmark has been added
        assertTrue(solo.searchText("Bookmark 1", true));
    }

    public void testSingleDeleteButton() {

        selectAndClickOnBookmarkButton();
        solo.clickOnText("Confirm");

        scrollingToBookmarkTab();
        solo.sleep(6000);
        ImageButton SingleDeleteBookmarkButton = (ImageButton) solo.getView("imgBookmarkDelete");
        solo.clickOnView(SingleDeleteBookmarkButton);
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Confirm");

        //Verifies the bookmark has been deleted
        assertFalse(solo.searchText("Bookmark 1", false));
    }

    public void testMultipleDeleteButton() {
        selectAndClickOnBookmarkButton();

        solo.clickOnText("Confirm");
        solo.sleep(6000);
        ImageButton AddBookmarkButton = (ImageButton) solo.getView("butBookmark");
        solo.clickOnView(AddBookmarkButton);
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Confirm");

        scrollingToBookmarkTab();
        solo.clickOnView(solo.getView(R.id.deleteBookmarks));
        CheckBox deleteCheckbox = (CheckBox) solo.getView("bookmarkCheckBox");
        solo.clickOnView(deleteCheckbox);
        solo.clickOnView(solo.getView(R.id.confirmDelete));
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Confirm");

        assertFalse(solo.searchText("Bookmark 1", false));
        assertTrue(solo.searchText("Bookmark 2", true));
    }

    public void selectAndClickOnBookmarkButton(){
        String query = "Hello Internet";

        openNavDrawer();
        solo.clickOnText("Add Podcast");
        solo.waitForDialogToOpen(5000);
        solo.waitForView(R.id.sliding_tabs);
        solo.enterText(0, query);
        solo.sendKey(Solo.ENTER);
        solo.waitForView(R.id.gridView);

        solo.clickOnText("Educational");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Subscribe");
        solo.sleep(5000);
        solo.clickOnText("Open Podcast");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Twelve Drummers Drumming");
        solo.waitForDialogToOpen(5000);
        solo.clickOnText("Stream");
        solo.sleep(6000);
        ImageButton AddBookmarkButton = (ImageButton) solo.getView("butBookmark");
        solo.clickOnView(AddBookmarkButton);
        solo.waitForDialogToOpen(5000);
    }

    public void scrollingToBookmarkTab(){
        Display display = solo.getCurrentActivity().getWindowManager().getDefaultDisplay();
        Point maxSize = new Point();
        display.getSize(maxSize);
        PointF startPoint1 = new PointF();
        startPoint1.x = maxSize.x - 40;
        startPoint1.y = maxSize.y/2;
        PointF endPoint1 = new PointF();
        endPoint1.x = 0;
        endPoint1.y = maxSize.y/2;

        solo.sleep(5000);
        solo.drag(startPoint1.x, endPoint1.x, startPoint1.y, endPoint1.y, 4);
        solo.sleep(5000);
        solo.drag(startPoint1.x, endPoint1.x, startPoint1.y, endPoint1.y, 4);
        solo.sleep(5000);
        solo.drag(startPoint1.x, endPoint1.x, startPoint1.y, endPoint1.y, 4);
        solo.sleep(5000);
    }

    public void testEditBookmark(){
        selectAndClickOnBookmarkButton();
        solo.clickOnText("Confirm");

        scrollingToBookmarkTab();
        ImageButton EditButton = (ImageButton) solo.getView("imgBookmarkEdit");
        solo.clickOnView(EditButton);
        solo.waitForDialogToOpen(5000);
        solo.clearEditText(0);
        solo.sleep(5000);
        solo.enterText(0,"My renamed bookmark");
        solo.sleep(5000);
        solo.clickOnText("Save Changes");

        assertTrue(solo.searchText("My renamed bookmark", true));
    }

}
