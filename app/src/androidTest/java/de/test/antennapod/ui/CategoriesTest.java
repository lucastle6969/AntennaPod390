package de.test.antennapod.ui;

import android.content.SharedPreferences;
import android.graphics.Point;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;
import android.view.Display;
import android.support.v7.widget.SearchView;
import com.robotium.solo.Solo;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.feed.Category;
import de.danoeh.antennapod.core.preferences.UserPreferences;
import de.danoeh.antennapod.core.storage.DBWriter;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

import static java.lang.Thread.sleep;

/**
 * User interface tests for Categories Feature
 */

public class CategoriesTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;
    private Context context;
    private UITestUtils uiTestUtils;
    private SharedPreferences prefs;

    public CategoriesTest() {
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
        insertUncategorized();
        adapter.close();

        // override first launch preference
        // do this BEFORE calling getActivity()!
        prefs = getInstrumentation().getTargetContext().getSharedPreferences(MainActivity.PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putBoolean(MainActivity.PREF_IS_FIRST_LAUNCH, false).commit();
        UserPreferences.setCategoryToggle(false);

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

    private void insertUncategorized() {
        Category uncategorizedCategory = new Category(PodDBAdapter.UNCATEGORIZED_CATEGORY_ID, PodDBAdapter.UNCATEGORIZED_CATEGORY_NAME);

        synchronized (this) {
            try {
                DBWriter.setCategory(uncategorizedCategory);
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void goingToSubscriptionPage() throws Exception{
        uiTestUtils.addLocalFeedData(true);
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.subscriptions_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.subscriptions_label), getActionbarTitle());
    }

    public void testCategories() throws Exception{
        goingToSubscriptionPage();
        solo.clickOnView(solo.getView(R.id.toggleCategoryView));

        // Create new category
        String testCategoryName = "Test";
        solo.clickOnView(solo.getView(R.id.addCategory));
        solo.clickOnText(solo.getString(R.string.category_hint));
        solo.enterText(0, testCategoryName);
        solo.clickOnText(solo.getString(R.string.confirm_label));

        assertTrue(solo.searchText(testCategoryName));

        // Move feed into newly created category
        String feedTitle = "Title 0";
        solo.clickLongOnText(feedTitle);
        solo.clickOnText(solo.getString(R.string.move_to_category_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.clickOnText(PodDBAdapter.UNCATEGORIZED_CATEGORY_NAME);

        assertTrue(solo.searchText(feedTitle));

        solo.clickOnText(PodDBAdapter.UNCATEGORIZED_CATEGORY_NAME);

        // Rename category
        String newCategoryName = "Comedy";
        solo.clickOnView(solo.getView(R.id.edit_category_button));
        solo.clickOnText(testCategoryName);
        solo.enterText(0, "");
        solo.enterText(0, newCategoryName);
        solo.clickOnText(solo.getString(R.string.confirm_label));

        assertTrue(solo.searchText(newCategoryName));

        // Delete category
        solo.clickOnView(solo.getView(R.id.edit_category_button));
        solo.clickOnView(solo.getImageButton(2));
        solo.clickOnText(solo.getString(R.string.confirm_label));

        solo.sleep(1000);
        assertFalse(solo.searchText(newCategoryName));

        // Create new category while moving feed to category
        solo.clickLongOnText(feedTitle);
        solo.clickOnText(solo.getString(R.string.move_to_category_label));
        solo.clickOnView(solo.getImageButton(1));
        solo.clickOnText(solo.getString(R.string.category_hint));
        solo.enterText(0, newCategoryName);
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));

        assertTrue(solo.searchText(newCategoryName));

        // Unsubscribe to a feed which is in a category
        solo.clickLongOnText(feedTitle);
        solo.clickOnText(solo.getString(R.string.remove_feed_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));

        solo.sleep(1000);
        assertFalse(solo.searchText(feedTitle));

    }


    public void testCategorySearch() throws Exception{
        goingToSubscriptionPage();
        solo.clickOnView(solo.getView(R.id.search_button));
        solo.clickOnText(solo.getString(R.string.search_subscription));
        solo.enterText(0, "2");
        solo.sendKey(Solo.ENTER);
        assertFalse(solo.searchText("Title 1", true));
        solo.clickOnText("Title 2");
    }


    public void testToggleBetweenCategoryAndAllFeedsView() {
        try {
            goingToSubscriptionPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        solo.clickOnView(solo.getView(R.id.toggleCategoryView));
        solo.sleep(2000);
        assertTrue(solo.searchText("Uncategorized Section", true));
    }

    public void testCollapseCategoryView(){
        String title = "new bookmark";

        try {
            goingToSubscriptionPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
        solo.clickOnView(solo.getView(R.id.toggleCategoryView));
        solo.clickOnView(solo.getView(R.id.addCategory));

        solo.waitForDialogToOpen();
        solo.enterText(0, title);
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.sleep(2000);
        solo.clickLongOnText("Title 1");
        solo.clickOnText(solo.getString(R.string.move_to_category_label));
        solo.clickOnText(solo.getString(R.string.confirm_label));
        solo.clickOnText(title);
        assertFalse(solo.searchText("Title 1", true));
    }

}
