package de.test.antennapod.ui;

import android.content.SharedPreferences;
import android.test.ActivityInstrumentationTestCase2;

import android.content.Context;

import com.robotium.solo.Solo;

import de.danoeh.antennapod.R;
import de.danoeh.antennapod.activity.MainActivity;
import de.danoeh.antennapod.core.storage.PodDBAdapter;

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

    private void goingToSubscriptionPage() throws Exception{
        uiTestUtils.addLocalFeedData(true);
        openNavDrawer();
        solo.clickOnText(solo.getString(R.string.subscriptions_label));
        solo.waitForView(android.R.id.list);
        assertEquals(solo.getString(R.string.subscriptions_label), getActionbarTitle());

        solo.sleep(4000);
    }

    public void testGoToSubscriptionsPage() throws Exception{
        
      goingToSubscriptionPage();

    }

    public void testRenameCategoryValidation() {
        //goingToSubscriptionPage();
        //TODO: click on pen icon, erase the name of the category and try to save a blank category name.
        //solo.clickOnText("Confirm");
        //TODO: assert
    }

    public void testRenameCategorySuccess() {
        //goingToSubscriptionPage();
        //TODO: click on pen icon, write a new category title, and save
        //solo.clickOnText("Confirm");
        //TODO: assert
    }

    public void testMoveFeedToDifferentCategory() {
        //goingToSubscriptionPage();
        //TODO: clickLong on a podcast in a Category, and selecting a new category
        //solo.clickOnText("Confirm");
    }
}
