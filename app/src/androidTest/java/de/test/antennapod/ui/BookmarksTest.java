package de.test.antennapod.ui;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.PointF;
import android.test.FlakyTest;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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
        ImageButton AddBookmarkButton = (ImageButton) solo.getView("butBookmark");
        solo.clickOnView(AddBookmarkButton);
        solo.waitForDialogToOpen();
        
        assertTrue(solo.searchText("Set a bookmark", true));

    }

    public void testAddBookmark() {
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
        ImageButton AddBookmarkButton = (ImageButton) solo.getView("butBookmark");
        solo.clickOnView(AddBookmarkButton);
        solo.waitForDialogToOpen();
        solo.clickOnText("Confirm");

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

        assertTrue(solo.searchText("Bookmark 1", true));
    }

    public void testSingleDeleteButton() {
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
        ImageButton AddBookmarkButton = (ImageButton) solo.getView("butBookmark");
        solo.clickOnView(AddBookmarkButton);
        solo.waitForDialogToOpen();
        solo.clickOnText("Confirm");

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

        ImageButton SingleDeleteBookmarkButton = (ImageButton) solo.getView("imgBookmarkDelete");
        solo.clickOnView(SingleDeleteBookmarkButton);
        solo.waitForDialogToOpen();
        solo.clickOnText("Confirm");

        assertFalse(solo.searchText("Bookmark 1", false));
    }

}
